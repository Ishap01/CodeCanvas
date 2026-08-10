import {
    useCallback,
    useEffect,
    useMemo,
    useState,
} from "react";

import {
    Link,
    useNavigate,
} from "react-router-dom";

import {
    FaChartLine,
    FaCode,
    FaHashtag,
    FaHome,
    FaRegBookmark,
    FaSyncAlt,
    FaUsers,
} from "react-icons/fa";

import SnippetCard from "../../../components/snippets/SnippetCard/SnippetCard";

import {
    getProfile,
} from "../../../services/userService";

import {
    getUserStatistics,
} from "../../../services/statisticsService";

import {
    getMySnippets,
    getPublicSnippets,
} from "../../../services/snippetService";

import "./UserDashboard.css";

const DASHBOARD_FEED_LIMIT = 12;

/*
 * API response kabhi direct hota hai:
 *
 * [
 *     {...}
 * ]
 *
 * Ya wrapper ke andar hota hai:
 *
 * {
 *     data: [...]
 * }
 */
function unwrapResponseData(response) {
    if (response == null) {
        return null;
    }

    if (
        typeof response === "object" &&
        response.data !== undefined
    ) {
        return response.data;
    }

    return response;
}

/*
 * Different possible API response structures se
 * snippet array extract karta hai.
 */
function extractSnippetArray(response) {
    const payload =
        unwrapResponseData(response);

    if (Array.isArray(payload)) {
        return payload;
    }

    if (
        Array.isArray(
            payload?.snippets
        )
    ) {
        return payload.snippets;
    }

    if (
        Array.isArray(
            payload?.content
        )
    ) {
        return payload.content;
    }

    if (
        Array.isArray(
            payload?.results
        )
    ) {
        return payload.results;
    }

    return [];
}

/*
 * Backend ke different field names ko
 * frontend ke standard SnippetCard format mein
 * normalize karta hai.
 */
function normalizeSnippet(snippet) {
    return {
        ...snippet,

        snippetId:
            snippet?.snippetId ||
            snippet?.id,

        userId:
            snippet?.userId ||
            snippet?.ownerId ||
            snippet?.createdByUserId ||
            null,

        title:
            snippet?.title ||
            "Untitled snippet",

        description:
            snippet?.description ||
            "",

        code:
            snippet?.code ||
            "",

        language:
            snippet?.language ||
            "Code",

        framework:
            snippet?.framework ||
            "",

        categoryName:
            snippet?.categoryName ||
            snippet?.category ||
            "General",

        visibility:
            String(
                snippet?.visibility ||
                ""
            )
                .trim()
                .toUpperCase(),

        tags:
            Array.isArray(
                snippet?.tags
            )
                ? snippet.tags
                : [],

        previewImageUrl:
            snippet?.previewImageUrl ||
            snippet?.imageUrl ||
            null,

        likeCount:
            getNumberValue(
                snippet?.likeCount ??
                snippet?.likes
            ),

        commentCount:
            getNumberValue(
                snippet?.commentCount ??
                snippet?.comments
            ),

        bookmarkCount:
            getNumberValue(
                snippet?.bookmarkCount ??
                snippet?.bookmarks
            ),

        viewCount:
            getNumberValue(
                snippet?.viewCount ??
                snippet?.views
            ),

        forkCount:
            getNumberValue(
                snippet?.forkCount ??
                snippet?.forks
            ),

        createdAt:
            snippet?.createdAt ||
            null,
    };
}

/*
 * Popularity calculation:
 *
 * Like      = 5 points
 * Fork      = 6 points
 * Bookmark  = 4 points
 * Comment   = 3 points
 * View      = 1 point
 */
function calculatePopularityScore(snippet) {
    return (
        getNumberValue(
            snippet?.likeCount
        ) * 5 +

        getNumberValue(
            snippet?.forkCount
        ) * 6 +

        getNumberValue(
            snippet?.bookmarkCount
        ) * 4 +

        getNumberValue(
            snippet?.commentCount
        ) * 3 +

        getNumberValue(
            snippet?.viewCount
        )
    );
}

function getSnippetTimestamp(snippet) {
    if (!snippet?.createdAt) {
        return 0;
    }

    const timestamp =
        new Date(
            snippet.createdAt
        ).getTime();

    return Number.isNaN(timestamp)
        ? 0
        : timestamp;
}

/*
 * Duplicate snippet IDs remove karta hai.
 */
function removeDuplicateSnippets(snippets) {
    const uniqueSnippets =
        new Map();

    snippets.forEach((snippet) => {
        if (!snippet?.snippetId) {
            return;
        }

        uniqueSnippets.set(
            String(
                snippet.snippetId
            ),
            snippet
        );
    });

    return Array.from(
        uniqueSnippets.values()
    );
}

export default function UserDashboard() {
    const navigate =
        useNavigate();

    const [profile, setProfile] =
        useState(null);

    const [
        statistics,
        setStatistics,
    ] = useState(null);

    /*
     * Logged-in user ke apne snippets.
     * Welcome statistics ke liye use honge.
     */
    const [
        mySnippets,
        setMySnippets,
    ] = useState([]);

    /*
     * Dashboard community feed.
     *
     * Ismein sirf PUBLIC snippets honge.
     */
    const [
        feedSnippets,
        setFeedSnippets,
    ] = useState([]);

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

    const [
        refreshing,
        setRefreshing,
    ] = useState(false);

    const [error, setError] =
        useState("");

    const loadDashboard =
        useCallback(
            async (
                showRefreshState = false
            ) => {
                try {
                    if (
                        showRefreshState
                    ) {
                        setRefreshing(true);
                    } else {
                        setLoading(true);
                    }

                    setError("");

                    const [
                        profileResult,
                        mySnippetResult,
                        publicSnippetResult,
                    ] = await Promise.allSettled([
                        getProfile(),
                        getMySnippets(),
                        getPublicSnippets(),
                    ]);

                    

                    const profileResponse =
                        profileResult.status === "fulfilled"
                            ? profileResult.value
                            : null;

                    const mySnippetResponse =
                        mySnippetResult.status === "fulfilled"
                            ? mySnippetResult.value
                            : null;

                    const publicSnippetResponse =
                        publicSnippetResult.status === "fulfilled"
                            ? publicSnippetResult.value
                            : null;

                    if (!profileResponse) {
    throw new Error("Unable to load profile.");
}

                    const profilePayload =
                        unwrapResponseData(
                            profileResponse
                        );

                    const normalizedProfile =
                        profilePayload?.user ||
                        profilePayload?.profile ||
                        profilePayload;

                    /*
                     * Logged-in user's own snippets.
                     */
                    const normalizedMySnippets =
                        extractSnippetArray(
                            mySnippetResponse
                        )
                            .map(
                                normalizeSnippet
                            )
                            .filter(
                                (snippet) =>
                                    Boolean(
                                        snippet.snippetId
                                    )
                            );

                    /*
                     * Public endpoint response normalize karne
                     * ke baad strict visibility filter.
                     *
                     * Sirf PUBLIC allowed hai.
                     *
                     * PREMIUM remove.
                     * PRIVATE remove.
                     */
                    const normalizedPublicSnippets =
                        extractSnippetArray(
                            publicSnippetResponse
                        )
                            .map(
                                normalizeSnippet
                            )
                            .filter(
                                (snippet) =>
                                    Boolean(
                                        snippet.snippetId
                                    )
                            )
                            .filter(
                                (snippet) =>
                                    snippet.visibility ===
                                    "PUBLIC"
                            );

                    /*
                     * Logged-in user's apne public snippets
                     * community feed mein hide karne ki koshish.
                     *
                     * Agar backend snippet response userId nahi deta,
                     * tab snippet feed mein reh sakta hai.
                     */
                    const communitySnippets =
                        normalizedPublicSnippets.filter(
                            (snippet) => {
                                if (
                                    snippet.visibility !==
                                    "PUBLIC"
                                ) {
                                    return false;
                                }

                                if (
                                    !normalizedProfile
                                        ?.userId ||
                                    !snippet.userId
                                ) {
                                    return true;
                                }

                                return (
                                    String(
                                        snippet.userId
                                    ) !==
                                    String(
                                        normalizedProfile
                                            .userId
                                    )
                                );
                            }
                        );

                    /*
                     * Popularity ke according sort.
                     *
                     * Same score hone par newest snippet pehle.
                     */
                    const sortedPopularSnippets =
                        removeDuplicateSnippets(
                            communitySnippets
                        )
                            .sort(
                                (
                                    firstSnippet,
                                    secondSnippet
                                ) => {
                                    const scoreDifference =
                                        calculatePopularityScore(
                                            secondSnippet
                                        ) -
                                        calculatePopularityScore(
                                            firstSnippet
                                        );

                                    if (
                                        scoreDifference !==
                                        0
                                    ) {
                                        return scoreDifference;
                                    }

                                    return (
                                        getSnippetTimestamp(
                                            secondSnippet
                                        ) -
                                        getSnippetTimestamp(
                                            firstSnippet
                                        )
                                    );
                                }
                            )
                            .slice(
                                0,
                                DASHBOARD_FEED_LIMIT
                            );

                    setProfile(
                        normalizedProfile
                    );

                    setMySnippets(
                        normalizedMySnippets
                    );

                    setFeedSnippets(
                        sortedPopularSnippets
                    );

                    /*
                     * Statistics request fail hone par bhi
                     * dashboard feed load rahegi.
                     */
                    if (
                        normalizedProfile?.userId
                    ) {
                        try {
                            const statisticsResponse =
                                await getUserStatistics(
                                    normalizedProfile
                                        .userId
                                );

                            setStatistics(
                                unwrapResponseData(
                                    statisticsResponse
                                )
                            );
                        } catch (
                        statisticsError
                        ) {
                            console.error(
                                "Unable to load dashboard statistics:",
                                statisticsError
                            );

                            setStatistics({
                                totalSnippets:
                                    normalizedMySnippets
                                        .length,

                                totalViews: 0,
                                totalLikes: 0,
                                totalFavorites: 0,
                                followers: 0,
                                following: 0,
                            });
                        }
                    }
                } catch (
                requestError
                ) {
                    console.error(
                        "Unable to load dashboard:",
                        requestError
                    );

                    setError(
                        requestError
                            ?.response
                            ?.data
                            ?.message ||
                        requestError
                            ?.message ||
                        "Unable to load dashboard."
                    );

                    setProfile(null);
                    setMySnippets([]);
                    setFeedSnippets([]);
                } finally {
                    setLoading(false);
                    setRefreshing(false);
                }
            },
            []
        );

    useEffect(() => {
        loadDashboard();
    }, [loadDashboard]);

    /*
     * Public feed se language filters.
     */
    const languageFilters =
        useMemo(() => {
            const languages =
                new Set();

            feedSnippets.forEach(
                (snippet) => {
                    const language =
                        String(
                            snippet?.language ||
                            ""
                        ).trim();

                    if (language) {
                        languages.add(
                            language
                        );
                    }
                }
            );

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
        }, [feedSnippets]);

    /*
     * Public feed se framework filters.
     */
    const frameworkFilters =
        useMemo(() => {
            const frameworks =
                new Set();

            feedSnippets.forEach(
                (snippet) => {
                    const framework =
                        String(
                            snippet?.framework ||
                            ""
                        ).trim();

                    if (framework) {
                        frameworks.add(
                            framework
                        );
                    }
                }
            );

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
        }, [feedSnippets]);

    /*
     * Logged-in user's own snippet statistics.
     */
    const mySnippetStatistics =
        useMemo(() => {
            return mySnippets.reduce(
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
        }, [mySnippets]);

    /*
     * Language and framework filters.
     *
     * Visibility safety check yahan bhi rakha hai.
     */
    const filteredFeed =
        useMemo(() => {
            return feedSnippets.filter(
                (snippet) => {
                    const visibility =
                        String(
                            snippet?.visibility ||
                            ""
                        )
                            .trim()
                            .toUpperCase();

                    if (
                        visibility !==
                        "PUBLIC"
                    ) {
                        return false;
                    }

                    const snippetLanguage =
                        String(
                            snippet?.language ||
                            ""
                        )
                            .trim()
                            .toLowerCase();

                    const snippetFramework =
                        String(
                            snippet?.framework ||
                            ""
                        )
                            .trim()
                            .toLowerCase();

                    const languageMatch =
                        selectedLanguage ===
                        "All" ||
                        snippetLanguage ===
                        selectedLanguage
                            .trim()
                            .toLowerCase();

                    const frameworkMatch =
                        selectedFramework ===
                        "All" ||
                        snippetFramework ===
                        selectedFramework
                            .trim()
                            .toLowerCase();

                    return (
                        languageMatch &&
                        frameworkMatch
                    );
                }
            );
        }, [
            feedSnippets,
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

                    <section className="dashboardLoadingState">

                        <span className="dashboardLoadingSpinner" />

                        <h2>
                            Loading your dashboard
                        </h2>

                        <p>
                            Fetching your profile,
                            statistics and public
                            community feed.
                        </p>

                    </section>

                </main>

            </div>
        );
    }

    if (error) {
        return (
            <div className="userDashboardPage">

                <main className="userDashboardContent">

                    <section className="dashboardState">

                        <h2>
                            Unable to load dashboard
                        </h2>

                        <p>
                            {error}
                        </p>

                        <button
                            type="button"
                            onClick={() =>
                                loadDashboard()
                            }
                        >
                            Try Again
                        </button>

                    </section>

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

                    <Link
                        to="/dashboard"
                        className="activeSidebarLink"
                    >
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
                    ].map(
                        (category) => (
                            <Link
                                to={`/search?q=${encodeURIComponent(
                                    category
                                )}`}
                                key={category}
                            >
                                <FaHashtag />

                                {category}
                            </Link>
                        )
                    )}

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
                            Welcome back
                        </span>

                        <h1>
                            {profile.fullName}
                        </h1>

                        <p>
                            Discover popular public
                            code shared by the
                            CodeCanvas community.
                        </p>

                    </div>

                    <div className="welcomeStats">

                        <span>
                            <strong>
                                {
                                    mySnippetStatistics
                                        .totalSnippets
                                }
                            </strong>{" "}
                            your snippets
                        </span>

                        <span>
                            <strong>
                                {getNumberValue(
                                    statistics?.followers
                                )}
                            </strong>{" "}
                            followers
                        </span>

                        <span>
                            <strong>
                                {
                                    mySnippetStatistics
                                        .totalViews
                                }
                            </strong>{" "}
                            total views
                        </span>

                        <span>
                            <strong>
                                {
                                    mySnippetStatistics
                                        .totalLikes
                                }
                            </strong>{" "}
                            total likes
                        </span>

                    </div>

                </section>

                {/* ================= FEED HEADER ================= */}

                <section className="dashboardFeedHeader">

                    <div>

                        <span>
                            <FaChartLine />

                            COMMUNITY FEED
                        </span>

                        <h2>
                            Popular public snippets
                        </h2>

                        <p>
                            Only public snippets are
                            displayed here. Premium and
                            private snippets are excluded.
                        </p>

                    </div>

                    <div className="dashboardFeedActions">

                        <button
                            type="button"
                            className="dashboardRefreshButton"
                            disabled={refreshing}
                            onClick={() =>
                                loadDashboard(true)
                            }
                        >
                            <FaSyncAlt
                                className={
                                    refreshing
                                        ? "dashboardRefreshingIcon"
                                        : ""
                                }
                            />

                            {refreshing
                                ? "Refreshing..."
                                : "Refresh"}
                        </button>

                        <button
                            type="button"
                            className="dashboardExploreButton"
                            onClick={() =>
                                navigate(
                                    "/snippets"
                                )
                            }
                        >
                            <FaCode />

                            Explore all
                        </button>

                    </div>

                </section>

                {/* ================= EMPTY FEED ================= */}

                {feedSnippets.length ===
                    0 && (

                        <section className="dashboardState">

                            <FaCode className="dashboardStateIcon" />

                            <h2>
                                No public snippets found
                            </h2>

                            <p>
                                Community members have not
                                shared public snippets yet.
                            </p>

                            <button
                                type="button"
                                onClick={() =>
                                    navigate(
                                        "/snippets/create"
                                    )
                                }
                            >
                                Create a Snippet
                            </button>

                        </section>
                    )}

                {/* ================= FILTERED EMPTY ================= */}

                {feedSnippets.length >
                    0 &&
                    filteredFeed.length ===
                    0 && (

                        <section className="dashboardState">

                            <h2>
                                No matching snippets
                            </h2>

                            <p>
                                No public snippets match the
                                selected language and
                                framework.
                            </p>

                            <button
                                type="button"
                                onClick={clearFilters}
                            >
                                Clear Filters
                            </button>

                        </section>
                    )}

                {/* ================= PUBLIC FEED ================= */}

                {filteredFeed.length >
                    0 && (

                        <section className="dashboardSnippetGrid">

                            {filteredFeed.map(
                                (snippet) => (
                                    <div
                                        key={
                                            snippet.snippetId
                                        }
                                        className="dashboardSnippetCardWrapper"
                                    >
                                        <SnippetCard
                                            snippet={
                                                snippet
                                            }
                                            showOwnerActions={
                                                false
                                            }
                                            showBookmarkAction={
                                                true
                                            }
                                        />
                                    </div>
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