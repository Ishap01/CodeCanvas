import React, {
    useEffect,
    useMemo,
    useState,
} from "react";

import "./UserDashboard.css";

import {
    FaChartLine,
    FaCodeBranch,
    FaHashtag,
    FaHome,
    FaRegBookmark,
    FaRegComment,
    FaRegHeart,
    FaStar,
    FaUsers,
} from "react-icons/fa";

import {
    Link,
    useNavigate,
} from "react-router-dom";

import {
    getProfile,
} from "../../../services/userService";

import {
    getUserStatistics,
} from "../../../services/statisticsService";

import {
    getMySnippets,
} from "../../../services/snippetService";

export default function UserDashboard() {

    const navigate = useNavigate();

    const [profile, setProfile] =
        useState(null);

    const [statistics, setStatistics] =
        useState(null);

    const [snippets, setSnippets] =
        useState([]);

    const [
        selectedLanguage,
        setSelectedLanguage,
    ] = useState("All");

    const [
        selectedFramework,
        setSelectedFramework,
    ] = useState("All");

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    useEffect(() => {

        const loadDashboard = async () => {

            try {

                setLoading(true);
                setError("");

                /*
                 * Profile aur snippets independently
                 * load ho sakte hain, isliye Promise.all.
                 */
                const [
                    profileData,
                    snippetData,
                ] = await Promise.all([
                    getProfile(),
                    getMySnippets(),
                ]);

                setProfile(profileData);

                setSnippets(
                    Array.isArray(snippetData)
                        ? snippetData
                        : []
                );

                /*
                 * Statistics request profile userId
                 * milne ke baad call hogi.
                 */
                try {

                    const statisticsData =
                        await getUserStatistics(
                            profileData.userId
                        );

                    setStatistics(
                        statisticsData
                    );

                } catch (
                    statisticsError
                ) {

                    console.error(
                        "Unable to load dashboard statistics:",
                        statisticsError
                    );

                    /*
                     * Statistics service fail hone par bhi
                     * dashboard aur snippets dikhte rahenge.
                     */
                    setStatistics({
                        totalSnippets: 0,
                        totalViews: 0,
                        totalLikes: 0,
                        totalFavorites: 0,
                        followers: 0,
                        following: 0,
                    });
                }

            } catch (requestError) {

                console.error(
                    "Unable to load dashboard:",
                    requestError
                );

                setError(
                    requestError?.response?.data
                        ?.message ||
                    requestError?.message ||
                    "Unable to load dashboard."
                );

                setProfile(null);
                setSnippets([]);

            } finally {

                setLoading(false);

            }

        };

        loadDashboard();

    }, []);

    /*
     * Real snippets se available languages
     * automatically niklenge.
     */
    const languageFilters =
        useMemo(() => {

            const languages =
                new Set();

            snippets.forEach((snippet) => {

                const language =
                    snippet?.language?.trim();

                if (language) {
                    languages.add(language);
                }

            });

            return [
                "All",
                ...Array.from(
                    languages
                ).sort(
                    (
                        firstLanguage,
                        secondLanguage
                    ) =>
                        firstLanguage.localeCompare(
                            secondLanguage
                        )
                ),
            ];

        }, [snippets]);

    /*
     * Real snippets se frameworks
     * automatically niklenge.
     */
    const frameworkFilters =
        useMemo(() => {

            const frameworks =
                new Set();

            snippets.forEach((snippet) => {

                const framework =
                    snippet?.framework?.trim();

                if (framework) {
                    frameworks.add(framework);
                }

            });

            return [
                "All",
                ...Array.from(
                    frameworks
                ).sort(
                    (
                        firstFramework,
                        secondFramework
                    ) =>
                        firstFramework.localeCompare(
                            secondFramework
                        )
                ),
            ];

        }, [snippets]);

    /*
     * My Snippets ke actual data se
     * dashboard totals calculate honge.
     */
    const snippetStatistics =
        useMemo(() => {

            return snippets.reduce(
                (
                    calculatedStatistics,
                    snippet
                ) => {

                    calculatedStatistics
                        .totalSnippets += 1;

                    calculatedStatistics
                        .totalViews +=
                        getNumberValue(
                            snippet?.viewCount
                        );

                    calculatedStatistics
                        .totalLikes +=
                        getNumberValue(
                            snippet?.likeCount
                        );

                    calculatedStatistics
                        .totalBookmarks +=
                        getNumberValue(
                            snippet?.bookmarkCount
                        );

                    calculatedStatistics
                        .totalComments +=
                        getNumberValue(
                            snippet?.commentCount
                        );

                    calculatedStatistics
                        .totalForks +=
                        getNumberValue(
                            snippet?.forkCount
                        );

                    return calculatedStatistics;

                },
                {
                    totalSnippets: 0,
                    totalViews: 0,
                    totalLikes: 0,
                    totalBookmarks: 0,
                    totalComments: 0,
                    totalForks: 0,
                }
            );

        }, [snippets]);

    const filteredSnippets =
        useMemo(() => {

            return snippets.filter(
                (snippet) => {

                    const languageMatch =
                        selectedLanguage ===
                            "All" ||
                        snippet?.language ===
                            selectedLanguage;

                    const frameworkMatch =
                        selectedFramework ===
                            "All" ||
                        snippet?.framework ===
                            selectedFramework;

                    return (
                        languageMatch &&
                        frameworkMatch
                    );

                }
            );

        }, [
            snippets,
            selectedLanguage,
            selectedFramework,
        ]);

    const clearFilters = () => {
        setSelectedLanguage("All");
        setSelectedFramework("All");
    };

    if (loading) {

        return (
            <div className="userDashboardPage">

                <main className="userDashboardContent">

                    <div className="dashboardState">

                        <h2>
                            Loading dashboard...
                        </h2>

                        <p>
                            Please wait while your
                            profile and snippets are
                            loaded.
                        </p>

                    </div>

                </main>

            </div>
        );

    }

    if (error) {

        return (
            <div className="userDashboardPage">

                <main className="userDashboardContent">

                    <div className="dashboardState">

                        <h2>
                            Unable to load dashboard
                        </h2>

                        <p>
                            {error}
                        </p>

                        <button
                            type="button"
                            onClick={() =>
                                window.location.reload()
                            }
                        >
                            Try Again
                        </button>

                    </div>

                </main>

            </div>
        );

    }

    if (!profile) {
        return null;
    }

    return (
        <div className="userDashboardPage">

            {/* ================= SIDEBAR ================= */}

            <aside className="userSidebar">

                <p className="sidebarHeading">
                    NAVIGATION
                </p>

                <nav className="sidebarNavigation">

                    <Link to="/dashboard">
                        <FaHome />
                        Home
                    </Link>

                    <Link to="/search">
                        <FaChartLine />
                        Explore
                    </Link>

                    <Link to="/snippets/bookmarks">
                        <FaRegBookmark />
                        Saved
                    </Link>

                    <Link to="/profile">
                        <FaUsers />
                        Profile
                    </Link>

                </nav>

                <div className="sidebarDivider" />

                <p className="sidebarHeading">
                    CATEGORIES
                </p>

                <nav className="sidebarNavigation">

                    {[
                        "Frontend",
                        "Backend",
                        "DevOps",
                        "Database",
                        "Mobile",
                    ].map((category) => (

                        <Link
                            to={`/search?q=${encodeURIComponent(
                                category
                            )}`}
                            key={category}
                        >
                            <FaHashtag />

                            {category}
                        </Link>

                    ))}

                </nav>

            </aside>

            {/* ================= MAIN CONTENT ================= */}

            <main className="userDashboardContent">

                {/* ================= FILTERS ================= */}

                <section className="dashboardFilters">

                    <div className="filterRow">

                        <span className="filterLabel">
                            LANGUAGE:
                        </span>

                        <div className="filterOptions">

                            {languageFilters.map(
                                (language) => (

                                    <button
                                        key={language}
                                        type="button"
                                        className={
                                            selectedLanguage ===
                                            language
                                                ? "activeFilter"
                                                : ""
                                        }
                                        onClick={() =>
                                            setSelectedLanguage(
                                                language
                                            )
                                        }
                                    >
                                        {language}
                                    </button>

                                )
                            )}

                        </div>

                    </div>

                    <div className="filterRow">

                        <span className="filterLabel">
                            FRAMEWORK:
                        </span>

                        <div className="filterOptions">

                            {frameworkFilters.map(
                                (framework) => (

                                    <button
                                        key={framework}
                                        type="button"
                                        className={
                                            selectedFramework ===
                                            framework
                                                ? "activeFilter"
                                                : ""
                                        }
                                        onClick={() =>
                                            setSelectedFramework(
                                                framework
                                            )
                                        }
                                    >
                                        {framework}
                                    </button>

                                )
                            )}

                        </div>

                    </div>

                </section>

                {/* ================= WELCOME BANNER ================= */}

                <section className="userWelcomeBanner">

                    <div className="welcomeGradient" />

                    <div className="welcomeUser">

                        <span>
                            Hello
                        </span>

                        <h1>
                            {profile.fullName}
                        </h1>

                    </div>

                    <div className="welcomeStats">

                        <span>
                            <strong>
                                {
                                    snippetStatistics
                                        .totalSnippets
                                }
                            </strong>{" "}
                            snippets
                        </span>

                        <span>•</span>

                        <span>
                            <strong>
                                {getNumberValue(
                                    statistics?.followers
                                )}
                            </strong>{" "}
                            followers
                        </span>

                        <span>•</span>

                        <span>
                            <strong>
                                {
                                    snippetStatistics
                                        .totalViews
                                }
                            </strong>{" "}
                            total views
                        </span>

                        <span>•</span>

                        <span>
                            <strong>
                                {
                                    snippetStatistics
                                        .totalLikes
                                }
                            </strong>{" "}
                            total likes
                        </span>

                    </div>

                </section>

                {/* ================= EMPTY SNIPPET STATE ================= */}

                {snippets.length === 0 && (

                    <section className="dashboardState">

                        <h2>
                            No snippets yet
                        </h2>

                        <p>
                            Create your first snippet
                            to see it on your
                            dashboard.
                        </p>

                        <button
                            type="button"
                            onClick={() =>
                                navigate(
                                    "/snippets/create"
                                )
                            }
                        >
                            Create Snippet
                        </button>

                    </section>

                )}

                {/* ================= FILTERED EMPTY STATE ================= */}

                {snippets.length > 0 &&
                    filteredSnippets.length ===
                        0 && (

                    <section className="dashboardState">

                        <h2>
                            No matching snippets
                        </h2>

                        <p>
                            No snippets match the
                            selected language and
                            framework filters.
                        </p>

                        <button
                            type="button"
                            onClick={clearFilters}
                        >
                            Clear Filters
                        </button>

                    </section>

                )}

                {/* ================= REAL SNIPPET CARDS ================= */}

                {filteredSnippets.length > 0 && (

                    <section className="userSnippetGrid">

                        {filteredSnippets.map(
                            (snippet) => (

                                <article
                                    className="userSnippetCard"
                                    key={
                                        snippet.snippetId
                                    }
                                >

                                    <div className="userSnippetHeader">

                                        <div>

                                            <h2>
                                                {snippet.title}
                                            </h2>

                                            <span className="userLanguageBadge">
                                                {snippet.language}
                                            </span>

                                        </div>

                                        <button
                                            type="button"
                                            className="dashboardStarButton"
                                            aria-label={`Open ${snippet.title}`}
                                            onClick={() =>
                                                navigate(
                                                    `/snippets/${snippet.snippetId}`
                                                )
                                            }
                                        >
                                            <FaStar />
                                        </button>

                                    </div>

                                    <pre
                                        className="userCodePreview"
                                        onClick={() =>
                                            navigate(
                                                `/snippets/${snippet.snippetId}`
                                            )
                                        }
                                    >
                                        <code>
                                            {snippet.code}
                                        </code>
                                    </pre>

                                    <div className="userSnippetFooter">

                                        <div className="userSnippetStats">

                                            <span>
                                                <FaRegHeart />

                                                {getNumberValue(
                                                    snippet.likeCount
                                                )}
                                            </span>

                                            <span>
                                                <FaRegComment />

                                                {getNumberValue(
                                                    snippet.commentCount
                                                )}
                                            </span>

                                            <span>
                                                <FaCodeBranch />

                                                {getNumberValue(
                                                    snippet.forkCount
                                                )}
                                            </span>

                                        </div>

                                        <button
                                            type="button"
                                            className="userBookmarkButton"
                                            aria-label={`${snippet.bookmarkCount || 0} bookmarks`}
                                        >
                                            <FaRegBookmark />
                                        </button>

                                    </div>

                                </article>

                            )
                        )}

                    </section>

                )}

            </main>

        </div>
    );
}

function getNumberValue(value) {

    const numberValue =
        Number(value);

    return Number.isFinite(
        numberValue
    )
        ? numberValue
        : 0;
}