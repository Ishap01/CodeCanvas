import React, { useState } from "react";
import "./Profile.css";

import {
    FaUser,
    FaShareAlt,
    FaStar,
    FaRegHeart,
    FaRegComment,
    FaCodeBranch,
    FaRegBookmark,
} from "react-icons/fa";

import profileBanner from "../../../assets/images/hero.png";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

import {
    getProfile,
} from "../../../services/userService";

import {
    getUserStatistics,
} from "../../../services/statisticsService";


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
    const [profile, setProfile] = useState(null);

    const [statistics, setStatistics] = useState(null);

    const [snippets, setSnippets] = useState([]);

    const [loading, setLoading] = useState(true);

    const [error, setError] = useState("");

    const navigate = useNavigate();

    useEffect(() => {

        const loadProfile = async () => {

            try {

                setLoading(true);

                const profileData =
                    await getProfile();

                setProfile(profileData);

                const statisticsData =
                    await getUserStatistics(
                        profileData.userId
                    );

                setStatistics(statisticsData);

            } catch (err) {

                setError(
                    err.response?.data?.message ||
                    "Unable to load profile."
                );

            } finally {

                setLoading(false);

            }

        };

        loadProfile();

    }, []);


    const [activeTab, setActiveTab] =
        useState("Uploaded");

    const [following, setFollowing] =
        useState(false);

    const displayedSnippets =
        activeTab === "Uploaded"
            ? snippets
            : [];


    if (loading) {

        return (
            <div className="profilePage">

                Loading profile...

            </div>
        );

    }

    if (error) {

        return (
            <div className="profilePage">

                {error}

            </div>
        );

    }

    if (!profile || !statistics) {

        return null;

    }

    return (
        <div className="profilePage">

            {/* ================= PROFILE HEADER ================= */}

            <section className="profileHeader">

                <div
                    className="profileBanner"
                    style={{
                        backgroundImage: `url(${profileBanner})`,
                    }}
                >
                    <div className="profileBannerOverlay"></div>
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

                        <h1>{profile.fullName}</h1>

                        <p>
                            @{profile.username} · Full-stack Developer · Open Source Contributor
                        </p>

                        <div className="profileMainStats">

                            <span>
                                <strong>{statistics.totalSnippets}</strong> Snippets
                            </span>

                            <span>
                                <strong>{(statistics.followers).toLocaleString()}</strong> Followers
                            </span>

                            <span>
                                <strong>{statistics.following}</strong> Following
                            </span>

                        </div>

                    </div>

                    <div className="profileActions">

                        <button
                            type="button"
                            className="followButton"
                            onClick={() => navigate("/edit-profile")}
                        >
                            Edit Profile
                        </button>

                        <button
                            type="button"
                            className="shareProfileButton"
                            aria-label="Share Profile"
                        >
                            <FaShareAlt />
                        </button>

                    </div>

                </div>

            </section>

            {/* ================= PROFILE CONTENT ================= */}

            <main className="profileContent">

                {/* ABOUT */}

                <section className="profileAbout">

                    <p className="profileSectionLabel">
                        ABOUT
                    </p>

                    <p className="profileBio">
                        {profile.bio}
                    </p>

                    {profile.skills && profile.skills.length > 0 && (

                        <div className="profileSkills">

                            {profile.skills.map((skill) => (

                                <span key={skill}>
                                    {skill}
                                </span>

                            ))}

                        </div>

                    )}

                </section>

                {/* ================= TABS ================= */}

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

                    {/* ================= UPLOADED TAB ================= */}

                    {activeTab === "Uploaded" && (

                        displayedSnippets.length === 0 ? (

                            <div className="emptyProfileTab">

                                <h2>No Snippets Yet</h2>

                                <p>
                                    You haven't uploaded any snippets yet.
                                </p>

                            </div>

                        ) : (

                            <div className="profileSnippetGrid">

                                {displayedSnippets.map((snippet) => (

                                    <article
                                        className="profileSnippetCard"
                                        key={snippet.id}
                                    >

                                        <div className="profileSnippetHeader">

                                            <div>

                                                <h2>{snippet.title}</h2>

                                                <span className="profileLanguageBadge">
                                                    {snippet.language}
                                                </span>

                                            </div>

                                            <button
                                                type="button"
                                                className="profileStarButton"
                                                aria-label={`Star ${snippet.title}`}
                                            >
                                                <FaStar />
                                            </button>

                                        </div>

                                        <pre className="profileCodePreview">

                                            <code>{snippet.code}</code>

                                        </pre>

                                        <div className="profileSnippetFooter">

                                            <div className="profileSnippetStats">

                                                <span>
                                                    <FaRegHeart />
                                                    {snippet.likes}
                                                </span>

                                                <span>
                                                    <FaRegComment />
                                                    {snippet.comments}
                                                </span>

                                                <span>
                                                    <FaCodeBranch />
                                                    {snippet.forks}
                                                </span>

                                            </div>

                                            <button
                                                type="button"
                                                className="profileBookmarkButton"
                                                aria-label={`Save ${snippet.title}`}
                                            >
                                                <FaRegBookmark />
                                            </button>

                                        </div>

                                    </article>

                                ))}

                            </div>

                        )

                    )}

                    {/* ================= OTHER TABS ================= */}

                    {activeTab !== "Uploaded" && (

                        <div className="emptyProfileTab">

                            <h2>{activeTab}</h2>

                            <p>
                                No {activeTab.toLowerCase()} content available.
                            </p>

                            <button
                                type="button"
                                className="followButton"
                                onClick={() => setActiveTab("Uploaded")}
                            >
                                Back to Uploaded
                            </button>

                        </div>

                    )}

                </section>

            </main>

        </div>

    );
}