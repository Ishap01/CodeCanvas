import React, {
    useEffect,
    useMemo,
    useState,
} from "react";

import "./Profile.css";

import {
    FaShareAlt,
    FaUser,
} from "react-icons/fa";

import { useNavigate } from "react-router-dom";

import profileBanner from "../../../assets/images/hero.png";

import SnippetCard from "../../../components/snippets/SnippetCard/SnippetCard";

import {
    getProfile,
} from "../../../services/userService";

import {
    getUserStatistics,
} from "../../../services/statisticsService";

import {
    getMyBookmarkedSnippets,
    getMySnippets,
} from "../../../services/snippetService";

const tabs = [
    {
        key: "Uploaded",
        label: "Uploaded",
    },
    {
        key: "Saved",
        label: "Saved",
    },
    {
        key: "Liked",
        label: "Liked",
    },
    {
        key: "Activity",
        label: "Activity",
    },
];

export default function Profile() {

    const navigate = useNavigate();

    const [profile, setProfile] =
        useState(null);

    const [statistics, setStatistics] =
        useState(null);

    const [snippets, setSnippets] =
        useState([]);

    const [
        bookmarkedSnippets,
        setBookmarkedSnippets,
    ] = useState([]);

    const [activeTab, setActiveTab] =
        useState("Uploaded");

    const [loading, setLoading] =
        useState(true);

    const [
        savedTabLoading,
        setSavedTabLoading,
    ] = useState(false);

    const [error, setError] =
        useState("");

    const [
        savedTabError,
        setSavedTabError,
    ] = useState("");

    useEffect(() => {

        const loadProfilePage = async () => {

            try {

                setLoading(true);
                setError("");

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
                        "Unable to load user statistics:",
                        statisticsError
                    );

                    setStatistics({
                        followers: 0,
                        following: 0,
                    });
                }

            } catch (requestError) {

                console.error(
                    "Unable to load profile page:",
                    requestError
                );

                setError(
                    requestError?.response?.data
                        ?.message ||
                    requestError?.message ||
                    "Unable to load profile."
                );

            } finally {

                setLoading(false);

            }

        };

        loadProfilePage();

    }, []);

    useEffect(() => {

        if (
            activeTab !== "Saved" ||
            savedTabLoading ||
            bookmarkedSnippets.length > 0
        ) {
            return;
        }

        const loadSavedSnippets = async () => {

            try {

                setSavedTabLoading(true);
                setSavedTabError("");

                const response =
                    await getMyBookmarkedSnippets();

                setBookmarkedSnippets(
                    Array.isArray(response)
                        ? response
                        : []
                );

            } catch (requestError) {

                console.error(
                    "Unable to load saved snippets:",
                    requestError
                );

                setSavedTabError(
                    requestError?.message ||
                    "Unable to load saved snippets."
                );

            } finally {

                setSavedTabLoading(false);

            }

        };

        loadSavedSnippets();

    }, [
        activeTab,
        bookmarkedSnippets.length,
        savedTabLoading,
    ]);

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
                    totalForks: 0,
                }
            );

        }, [snippets]);

    const followers =
        getNumberValue(
            statistics?.followers
        );

    const following =
        getNumberValue(
            statistics?.following
        );

    const displayedSnippets =
        activeTab === "Uploaded"
            ? snippets
            : activeTab === "Saved"
                ? bookmarkedSnippets
                : [];

    const handleShareProfile =
        async () => {

            const profileUrl =
                window.location.href;

            try {

                if (navigator.share) {

                    await navigator.share({
                        title:
                            profile?.fullName ||
                            "CodeCanvas Profile",
                        text:
                            `View ${
                                profile?.fullName ||
                                "this user"
                            } on CodeCanvas.`,
                        url: profileUrl,
                    });

                    return;
                }

                await navigator.clipboard
                    .writeText(profileUrl);

                window.alert(
                    "Profile link copied."
                );

            } catch (shareError) {

                console.error(
                    "Unable to share profile:",
                    shareError
                );
            }
        };

    if (loading) {

        return (
            <div className="profilePage">

                <div className="emptyProfileTab">

                    <h2>
                        Loading profile...
                    </h2>

                    <p>
                        Please wait while your profile
                        is loaded.
                    </p>

                </div>

            </div>
        );
    }

    if (error) {

        return (
            <div className="profilePage">

                <div className="emptyProfileTab">

                    <h2>
                        Unable to load profile
                    </h2>

                    <p>
                        {error}
                    </p>

                    <button
                        type="button"
                        className="followButton"
                        onClick={() =>
                            window.location.reload()
                        }
                    >
                        Try Again
                    </button>

                </div>

            </div>
        );
    }

    if (!profile) {
        return null;
    }

    return (
        <div className="profilePage">

            {/* PROFILE HEADER */}

            <section className="profileHeader">

                <div
                    className="profileBanner"
                    style={{
                        backgroundImage:
                            `url(${profileBanner})`,
                    }}
                >
                    <div className="profileBannerOverlay" />
                </div>

                <div className="profileHeaderContent">

                    <div className="profileAvatar">

                        {profile.profileImage ? (

                            <img
                                src={
                                    profile.profileImage
                                }
                                alt={
                                    profile.fullName ||
                                    "Profile"
                                }
                            />

                        ) : (

                            <FaUser />

                        )}

                    </div>

                    <div className="profileIdentity">

                        <h1>
                            {profile.fullName}
                        </h1>

                        <p>
                            @{profile.username}
                            {" · "}
                            Full-stack Developer
                            {" · "}
                            Open Source Contributor
                        </p>

                        <div className="profileMainStats">

                            <span>
                                <strong>
                                    {
                                        snippetStatistics
                                            .totalSnippets
                                    }
                                </strong>
                                Snippets
                            </span>

                            <span>
                                <strong>
                                    {
                                        snippetStatistics
                                            .totalLikes
                                    }
                                </strong>
                                Likes
                            </span>

                            <span>
                                <strong>
                                    {
                                        snippetStatistics
                                            .totalBookmarks
                                    }
                                </strong>
                                Saves
                            </span>

                            <span>
                                <strong>
                                    {
                                        snippetStatistics
                                            .totalViews
                                    }
                                </strong>
                                Views
                            </span>

                            <span>
                                <strong>
                                    {
                                        snippetStatistics
                                            .totalForks
                                    }
                                </strong>
                                Forks
                            </span>

                            <span>
                                <strong>
                                    {
                                        followers
                                            .toLocaleString()
                                    }
                                </strong>
                                Followers
                            </span>

                            <span>
                                <strong>
                                    {
                                        following
                                            .toLocaleString()
                                    }
                                </strong>
                                Following
                            </span>

                        </div>

                    </div>

                    <div className="profileActions">

                        <button
                            type="button"
                            className="followButton"
                            onClick={() =>
                                navigate(
                                    "/edit-profile"
                                )
                            }
                        >
                            Edit Profile
                        </button>

                        <button
                            type="button"
                            className="shareProfileButton"
                            aria-label="Share Profile"
                            onClick={
                                handleShareProfile
                            }
                        >
                            <FaShareAlt />
                        </button>

                    </div>

                </div>

            </section>

            {/* PROFILE CONTENT */}

            <main className="profileContent">

                <section className="profileAbout">

                    <p className="profileSectionLabel">
                        ABOUT
                    </p>

                    <p className="profileBio">
                        {profile.bio?.trim()
                            ? profile.bio
                            : "No bio added yet."}
                    </p>

                    {Array.isArray(
                        profile.skills
                    ) &&
                        profile.skills.length >
                            0 && (

                        <div className="profileSkills">

                            {profile.skills.map(
                                (skill) => (

                                    <span
                                        key={skill}
                                    >
                                        {skill}
                                    </span>

                                )
                            )}

                        </div>

                    )}

                </section>

                <section className="profileTabsSection">

                    <div className="profileTabs">

                        {tabs.map((tab) => (

                            <button
                                key={tab.key}
                                type="button"
                                className={
                                    activeTab ===
                                    tab.key
                                        ? "activeProfileTab"
                                        : ""
                                }
                                onClick={() =>
                                    setActiveTab(
                                        tab.key
                                    )
                                }
                            >
                                {tab.label}
                            </button>

                        ))}

                    </div>

                    {(activeTab === "Uploaded" ||
                        activeTab === "Saved") && (
                        <>

                            {activeTab === "Saved" &&
                                savedTabLoading && (

                                <div className="emptyProfileTab">

                                    <h2>
                                        Loading saved snippets...
                                    </h2>

                                    <p>
                                        Please wait while saved
                                        snippets are loaded.
                                    </p>

                                </div>
                            )}

                            {activeTab === "Saved" &&
                                !savedTabLoading &&
                                savedTabError && (

                                <div className="emptyProfileTab">

                                    <h2>
                                        Unable to load saved
                                        snippets
                                    </h2>

                                    <p>
                                        {savedTabError}
                                    </p>

                                </div>
                            )}

                            {!savedTabLoading &&
                                !savedTabError &&
                                displayedSnippets.length ===
                                    0 && (

                                <div className="emptyProfileTab">

                                    <h2>
                                        {activeTab ===
                                        "Uploaded"
                                            ? "No Snippets Yet"
                                            : "No Saved Snippets"}
                                    </h2>

                                    <p>
                                        {activeTab ===
                                        "Uploaded"
                                            ? "You haven't uploaded any snippets yet."
                                            : "You haven't bookmarked any snippets yet."}
                                    </p>

                                </div>
                            )}

                            {!savedTabLoading &&
                                !savedTabError &&
                                displayedSnippets.length >
                                    0 && (

                                <div className="profileSnippetGrid">

                                    {displayedSnippets.map(
                                        (snippet) => (

                                            <SnippetCard
                                                key={
                                                    snippet
                                                        .snippetId
                                                }
                                                snippet={
                                                    snippet
                                                }
                                                showOwnerActions={
                                                    activeTab ===
                                                    "Uploaded"
                                                }
                                            />

                                        )
                                    )}

                                </div>
                            )}

                        </>
                    )}

                    {activeTab === "Liked" && (

                        <div className="emptyProfileTab">

                            <h2>
                                Liked Snippets
                            </h2>

                            <p>
                                Liked snippets endpoint
                                is not available yet.
                            </p>

                        </div>
                    )}

                    {activeTab === "Activity" && (

                        <div className="emptyProfileTab">

                            <h2>
                                Activity
                            </h2>

                            <p>
                                Recent activity will
                                appear here later.
                            </p>

                        </div>
                    )}

                </section>

            </main>

        </div>
    );
}

function getNumberValue(value) {

    const numberValue =
        Number(value);

    return Number.isFinite(numberValue)
        ? numberValue
        : 0;
}