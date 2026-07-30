import React from "react";
import "./UserDashboard.css";

import {
  FaHome,
  FaChartLine,
  FaRegBookmark,
  FaUsers,
  FaHashtag,
  FaStar,
  FaRegHeart,
  FaRegComment,
  FaCodeBranch,
  FaRegBookmark as FaBookmark,
} from "react-icons/fa";

const snippets = [
  {
    id: 1,
    title: "useLocalStorage Hook",
    language: "TypeScript",
    likes: 312,
    comments: 24,
    forks: 93,
  },
  {
    id: 2,
    title: "FastAPI Auth Middleware",
    language: "Python",
    likes: 198,
    comments: 17,
    forks: 59,
  },
  {
    id: 3,
    title: "Concurrent Worker Pool",
    language: "Go",
    likes: 267,
    comments: 31,
    forks: 80,
  },
  {
    id: 4,
    title: "Virtual Scroll List",
    language: "React",
    likes: 445,
    comments: 52,
    forks: 133,
  },
  {
    id: 5,
    title: "Safe String Parser",
    language: "Rust",
    likes: 189,
    comments: 11,
    forks: 56,
  },
  {
    id: 6,
    title: "Recursive CTE Pattern",
    language: "SQL",
    likes: 223,
    comments: 20,
    forks: 66,
  },
];

const languageFilters = [
  "All",
  "TypeScript",
  "Python",
  "Go",
  "Rust",
  "Java",
];

const frameworkFilters = [
  "All",
  "React",
  "Next.js",
  "FastAPI",
  "Gin",
  "Spring",
];

export default function UserDashboard() {
  return (
    <div className="userDashboardPage">
      {/* SIDEBAR */}

      <aside className="userSidebar">
        <p className="sidebarHeading">NAVIGATION</p>

        <nav className="sidebarNavigation">
          <a href="#home">
            <FaHome />
            Home
          </a>

          <a href="#trending">
            <FaChartLine />
            Trending
          </a>

          <a href="#saved">
            <FaRegBookmark />
            Saved
          </a>

          <a href="#following">
            <FaUsers />
            Following
          </a>
        </nav>

        <div className="sidebarDivider"></div>

        <p className="sidebarHeading">CATEGORIES</p>

        <nav className="sidebarNavigation">
          {["Frontend", "Backend", "DevOps", "Database", "Mobile"].map(
            (category) => (
              <a href={`#${category}`} key={category}>
                <FaHashtag />
                {category}
              </a>
            )
          )}
        </nav>
      </aside>

      {/* MAIN CONTENT */}

      <main className="userDashboardContent">
        {/* FILTERS */}

        <section className="dashboardFilters">
          <div className="filterRow">
            <span className="filterLabel">LANGUAGE:</span>

            <div className="filterOptions">
              {languageFilters.map((language, index) => (
                <button
                  type="button"
                  className={index === 0 ? "activeFilter" : ""}
                  key={language}
                >
                  {language}
                </button>
              ))}
            </div>
          </div>

          <div className="filterRow">
            <span className="filterLabel">FRAMEWORK:</span>

            <div className="filterOptions">
              {frameworkFilters.map((framework) => (
                <button type="button" key={framework}>
                  {framework}
                </button>
              ))}
            </div>
          </div>
        </section>

        {/* USER BANNER */}

        <section className="userWelcomeBanner">
          <div className="welcomeGradient"></div>

          <div className="welcomeUser">
            <span>GOOD MORNING</span>
            <h1>sarah.codes</h1>
          </div>

          <div className="welcomeStats">
            <span>
              <strong>142</strong> snippets
            </span>

            <span>•</span>

            <span>
              <strong>3.4k</strong> followers
            </span>

            <span>•</span>

            <span>
              <strong>1,240</strong> views this week
            </span>
          </div>
        </section>

        {/* CARDS */}

        <section className="userSnippetGrid">
          {snippets.map((snippet) => (
            <article className="userSnippetCard" key={snippet.id}>
              <div className="userSnippetHeader">
                <div>
                  <h2>{snippet.title}</h2>

                  <span className="userLanguageBadge">
                    {snippet.language}
                  </span>
                </div>

                <button
                  type="button"
                  className="dashboardStarButton"
                  aria-label={`Star ${snippet.title}`}
                >
                  <FaStar />
                </button>
              </div>

              <pre className="userCodePreview">
                <code>
{`const result = await api.fetch(
  endpoint, { cache: true }
);`}
                </code>
              </pre>

              <div className="userSnippetFooter">
                <div className="userSnippetStats">
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
                  className="userBookmarkButton"
                  aria-label={`Save ${snippet.title}`}
                >
                  <FaBookmark />
                </button>
              </div>
            </article>
          ))}
        </section>
      </main>
    </div>
  );
}