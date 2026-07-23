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

import profileBanner from "../../assets/hero.png";

const profileSnippets = [
  {
    id: 1,
    title: "useDebounce Hook",
    language: "TypeScript",
    likes: 889,
    comments: 74,
    forks: 266,
  },
  {
    id: 2,
    title: "Virtual Scroll List",
    language: "React",
    likes: 445,
    comments: 52,
    forks: 133,
  },
  {
    id: 3,
    title: "API Error Boundary",
    language: "TypeScript",
    likes: 312,
    comments: 28,
    forks: 93,
  },
  {
    id: 4,
    title: "Masonry Grid Layout",
    language: "CSS",
    likes: 267,
    comments: 19,
    forks: 80,
  },
  {
    id: 5,
    title: "JWT Auth Middleware",
    language: "Node.js",
    likes: 234,
    comments: 17,
    forks: 70,
  },
  {
    id: 6,
    title: "Zustand Store Pattern",
    language: "TypeScript",
    likes: 198,
    comments: 14,
    forks: 59,
  },
];

const tabs = ["Uploaded", "Saved", "Liked", "Activity"];

export default function Profile() {
  const [activeTab, setActiveTab] = useState("Uploaded");
  const [following, setFollowing] = useState(false);

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
          <div className="profileBannerOverlay"></div>
        </div>

        <div className="profileHeaderContent">
          <div className="profileAvatar">
            <FaUser />
          </div>

          <div className="profileIdentity">
            <h1>Sarah Chen</h1>

            <p>
              @sarah.codes · Full-stack dev · Open source contributor
            </p>

            <div className="profileMainStats">
              <span>
                <strong>142</strong> Snippets
              </span>

              <span>
                <strong>3,412</strong> Followers
              </span>

              <span>
                <strong>288</strong> Following
              </span>
            </div>
          </div>

          <div className="profileActions">
            <button
              type="button"
              className={
                following
                  ? "followButton followingButton"
                  : "followButton"
              }
              onClick={() => setFollowing(!following)}
            >
              {following ? "Following" : "Follow"}
            </button>

            <button
              type="button"
              className="shareProfileButton"
              aria-label="Share profile"
            >
              <FaShareAlt />
            </button>
          </div>
        </div>
      </section>

      {/* PROFILE CONTENT */}

      <main className="profileContent">
        {/* ABOUT */}

        <section className="profileAbout">
          <p className="profileSectionLabel">ABOUT</p>

          <p className="profileBio">
            TypeScript enthusiast. Building open-source developer tooling.
            Working on React and Node.js performance optimization. Currently at
            Vercel.
          </p>

          <div className="profileSkills">
            <span>TypeScript</span>
            <span>React</span>
            <span>Node.js</span>
            <span>PostgreSQL</span>
            <span>Docker</span>
          </div>
        </section>

        {/* TABS */}

        <section className="profileTabsSection">
          <div className="profileTabs">
            {tabs.map((tab) => (
              <button
                type="button"
                key={tab}
                className={activeTab === tab ? "activeProfileTab" : ""}
                onClick={() => setActiveTab(tab)}
              >
                {tab}
              </button>
            ))}
          </div>

          {/* UPLOADED TAB */}

          {activeTab === "Uploaded" && (
            <div className="profileSnippetGrid">
              {profileSnippets.map((snippet) => (
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
                    <code>
{`const result = await api.fetch(
  endpoint, { cache: true }
);`}
                    </code>
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
          )}

          {/* OTHER TABS */}

          {activeTab !== "Uploaded" && (
            <div className="emptyProfileTab">
              <p>No {activeTab.toLowerCase()} content available.</p>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}