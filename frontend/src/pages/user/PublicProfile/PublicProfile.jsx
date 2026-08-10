import React, {
    useEffect,
    useMemo,
    useState,
} from "react";

import "./PublicProfile.css";

import {
    FaShareAlt,
    FaUser,
} from "react-icons/fa";

import {
    useNavigate,
    useParams,
} from "react-router-dom";

import profileBanner from "../../../assets/images/hero.png";

import SnippetCard from "../../../components/snippets/SnippetCard/SnippetCard";

import {
    getPublicProfile,
} from "../../../services/userService";

import {
    getUserStatistics,
} from "../../../services/statisticsService";

import {
    getUserSnippets,
} from "../../../services/snippetService";

import {
    followUser,
    unfollowUser,
} from "../../../services/followService";

const tabs = [
    {
        key: "Uploaded",
        label: "Uploaded",
    },
];

export default function PublicProfile() {

    const navigate = useNavigate();

    const { username } = useParams();

    const [profile, setProfile] =
        useState(null);

    const [statistics, setStatistics] =
        useState(null);

    const [snippets, setSnippets] =
        useState([]);

    const [activeTab, setActiveTab] =
        useState("Uploaded");

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    const [isFollowing, setIsFollowing] =
        useState(false);

    useEffect(() => {

        const loadPublicProfile = async () => {

            try {

                setLoading(true);

                setError("");

                const profileData =
                    await getPublicProfile(
                        username
                    );

                if (
                    profileData.ownProfile
                ) {

                    navigate("/profile");

                    return;

                }

                setProfile(profileData);

                setIsFollowing(
                    profileData.following
                );

                const [
                    statisticsData,
                    snippetData,
                ] = await Promise.all([
                    getUserStatistics(
                        profileData.userId
                    ),
                    getUserSnippets(
                        profileData.userId
                    ),
                ]);

                setStatistics(
                    statisticsData
                );

                setSnippets(
                    Array.isArray(
                        snippetData
                    )
                        ? snippetData
                        : []
                );

            } catch (
                requestError
            ) {

                console.error(
                    requestError
                );

                setError(
                    requestError
                        ?.response?.data
                        ?.message ||
                    requestError.message ||
                    "Unable to load profile."
                );

            } finally {

                setLoading(false);

            }

        };

        loadPublicProfile();

    }, [username, navigate]);

    const snippetStatistics =
        useMemo(() => {

            return snippets.reduce(
                (
                    stats,
                    snippet
                ) => {

                    stats.totalSnippets++;

                    stats.totalViews +=
                        getNumberValue(
                            snippet.viewCount
                        );

                    stats.totalLikes +=
                        getNumberValue(
                            snippet.likeCount
                        );

                    stats.totalBookmarks +=
                        getNumberValue(
                            snippet.bookmarkCount
                        );

                    stats.totalForks +=
                        getNumberValue(
                            snippet.forkCount
                        );

                    return stats;

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

    const handleFollow =
        async () => {

            try {

                if (
                    isFollowing
                ) {

                    await unfollowUser(
                        profile.userId
                    );

                    setIsFollowing(
                        false
                    );

                    setStatistics(
                        previous => ({
                            ...previous,
                            followers:
                                previous.followers -
                                1,
                        })
                    );

                } else {

                    await followUser(
                        profile.userId
                    );

                    setIsFollowing(
                        true
                    );

                    setStatistics(
                        previous => ({
                            ...previous,
                            followers:
                                previous.followers +
                                1,
                        })
                    );

                }

            } catch (
                followError
            ) {

                console.error(
                    followError
                );

            }

        };

    const handleShareProfile =
        async () => {

            const profileUrl =
                window.location.href;

            try {

                if (
                    navigator.share
                ) {

                    await navigator.share({
                        title:
                            profile.fullName,
                        text:
                            `View ${profile.fullName}'s profile`,
                        url: profileUrl,
                    });

                    return;

                }

                await navigator.clipboard.writeText(
                    profileUrl
                );

                alert(
                    "Profile link copied."
                );

            } catch (
                shareError
            ) {

                console.error(
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
                        Please wait while
                        profile is loading.
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

    if (!profile)
        return null;

        return (
        <div className="profilePage">

            {/* PROFILE HEADER */}

            <section className="profileHeader">

                <div
                    className="profileBanner"
                    style={{
                        backgroundImage: `url(${profileBanner})`,
                    }}
                >
                    <div className="profileBannerOverlay" />
                </div>

                <div className="profileHeaderContent">

                    <div className="profileAvatar">

                        {profile.profileImage ? (

                            <img
                                src={profile.profileImage}
                                alt={profile.fullName}
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
                        </p>

                        <div className="profileMainStats">

                            <span>
                                <strong>
                                    {snippetStatistics.totalSnippets}
                                </strong>
                                Snippets
                            </span>

                            <span>
                                <strong>
                                    {snippetStatistics.totalLikes}
                                </strong>
                                Likes
                            </span>

                            <span>
                                <strong>
                                    {snippetStatistics.totalBookmarks}
                                </strong>
                                Saves
                            </span>

                            <span>
                                <strong>
                                    {snippetStatistics.totalViews}
                                </strong>
                                Views
                            </span>

                            <span>
                                <strong>
                                    {snippetStatistics.totalForks}
                                </strong>
                                Forks
                            </span>

                            <span>
                                <strong>
                                    {followers.toLocaleString()}
                                </strong>
                                Followers
                            </span>

                            <span>
                                <strong>
                                    {following.toLocaleString()}
                                </strong>
                                Following
                            </span>

                        </div>

                    </div>

                    <div className="profileActions">

                        <button
                            type="button"
                            className={
                                isFollowing
                                    ? "followingButton"
                                    : "followButton"
                            }
                            onClick={handleFollow}
                        >

                            {isFollowing
                                ? "Following"
                                : "Follow"}

                        </button>

                        <button
                            type="button"
                            className="shareProfileButton"
                            onClick={handleShareProfile}
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

                </section>

                <section className="profileTabsSection">

                    <div className="profileTabs">

                        {tabs.map((tab) => (

                            <button
                                key={tab.key}
                                type="button"
                                className={
                                    activeTab === tab.key
                                        ? "activeProfileTab"
                                        : ""
                                }
                                onClick={() =>
                                    setActiveTab(tab.key)
                                }
                            >

                                {tab.label}

                            </button>

                        ))}

                    </div>

                    {snippets.length === 0 ? (

                        <div className="emptyProfileTab">

                            <h2>
                                No Snippets Yet
                            </h2>

                            <p>

                                This user hasn't uploaded any snippets yet.

                            </p>

                        </div>

                    ) : (

                        <div className="profileSnippetGrid">

                            {snippets.map((snippet) => (

                                <SnippetCard
                                    key={snippet.snippetId}
                                    snippet={snippet}
                                    showOwnerActions={false}
                                />

                            ))}

                        </div>

                    )}

                </section>

            </main>

        </div>

    );

}

function getNumberValue(value) {

    const numberValue = Number(value);

    return Number.isFinite(numberValue)
        ? numberValue
        : 0;

}
