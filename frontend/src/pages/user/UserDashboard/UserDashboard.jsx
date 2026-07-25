import React, { useEffect, useState } from "react";
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

import {
  getProfile,
} from "../../../services/userService";

import {
  getUserStatistics,
} from "../../../services/statisticsService";


const mockSnippets = [
  {
    id: 1,
    title: "useLocalStorage Hook",
    language: "TypeScript",
    framework: "React",
    likes: 312,
    comments: 24,
    forks: 93,
    code: `const result = await api.fetch(
  endpoint, { cache: true }
);`,
  },
  {
    id: 2,
    title: "FastAPI Auth Middleware",
    language: "Python",
    framework: "FastAPI",
    likes: 198,
    comments: 17,
    forks: 59,
    code: `const result = await api.fetch(
  endpoint, { cache: true }
);`,
  },
  {
    id: 3,
    title: "Concurrent Worker Pool",
    language: "Go",
    framework: "Gin",
    likes: 267,
    comments: 31,
    forks: 80,
    code: `const result = await api.fetch(
  endpoint, { cache: true }
);`,
  },
  {
    id: 4,
    title: "Virtual Scroll List",
    language: "React",
    framework: "React",
    likes: 445,
    comments: 52,
    forks: 133,
    code: `const result = await api.fetch(
  endpoint, { cache: true }
);`,
  },
  {
    id: 5,
    title: "Safe String Parser",
    language: "Rust",
    framework: "All",
    likes: 189,
    comments: 11,
    forks: 56,
    code: `const result = await api.fetch(
  endpoint, { cache: true }
);`,
  },
  {
    id: 6,
    title: "Recursive CTE Pattern",
    language: "SQL",
    framework: "Spring",
    likes: 223,
    comments: 20,
    forks: 66,
    code: `const result = await api.fetch(
  endpoint, { cache: true }
);`,
  },
];

const languageFilters = [
  "All",
  "TypeScript",
  "Python",
  "Go",
  "Rust",
  "React",
  "SQL",
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
  const [profile, setProfile] = useState(null);

const [statistics, setStatistics] = useState(null);

const [snippets] = useState(mockSnippets);

useEffect(() => {

    const loadDashboard = async () => {

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
                "Unable to load dashboard."
            );

        } finally {

            setLoading(false);

        }

    };

    loadDashboard();

}, []);

const [loading, setLoading] = useState(true);

const [error, setError] = useState("");

  const [selectedLanguage, setSelectedLanguage] = useState("All");
  const [selectedFramework, setSelectedFramework] = useState("All");

  const filteredSnippets = snippets.filter((snippet) => {
    const languageMatch =
      selectedLanguage === "All" ||
      snippet.language === selectedLanguage;

    const frameworkMatch =
      selectedFramework === "All" ||
      snippet.framework === selectedFramework;

    return languageMatch && frameworkMatch;
  });

  if (loading) {

    return (

        <div className="userDashboardPage">

            Loading dashboard...

        </div>

    );

}

if (error) {

    return (

        <div className="userDashboardPage">

            {error}

        </div>

    );

}

if (!profile || !statistics) {

    return null;

}

  return (
    <div className="userDashboardPage">
      {/* Sidebar */}

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

      {/* Main Content */}

      <main className="userDashboardContent">
        {/* Filters */}

        <section className="dashboardFilters">
          <div className="filterRow">
            <span className="filterLabel">LANGUAGE:</span>

            <div className="filterOptions">
              {languageFilters.map((language) => (
                <button
                  key={language}
                  type="button"
                  className={
                    selectedLanguage === language
                      ? "activeFilter"
                      : ""
                  }
                  onClick={() =>
                    setSelectedLanguage(language)
                  }
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
                <button
                  key={framework}
                  type="button"
                  className={
                    selectedFramework === framework
                      ? "activeFilter"
                      : ""
                  }
                  onClick={() =>
                    setSelectedFramework(framework)
                  }
                >
                  {framework}
                </button>
              ))}
            </div>
          </div>
        </section>

        {/* Welcome Banner */}

        <section className="userWelcomeBanner">
          <div className="welcomeGradient"></div>

          <div className="welcomeUser">
            <span>Hello</span>

            <h1>{profile.fullName}</h1>
          </div>

          <div className="welcomeStats">
            <span>
              <strong>{statistics.totalSnippets}</strong> snippets
            </span>

            <span>•</span>

            <span>
              <strong>{statistics.followers}</strong> followers
            </span>

            <span>•</span>

            <span>
              <strong>{statistics.totalViews}</strong> total views
            </span>
          </div>
        </section>

        {/* Snippet Cards */}

        <section className="userSnippetGrid">
          {filteredSnippets.map((snippet) => (
            <article
              className="userSnippetCard"
              key={snippet.id}
            >
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
                >
                  <FaStar />
                </button>
              </div>

              <pre className="userCodePreview">
                <code>{snippet.code}</code>
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