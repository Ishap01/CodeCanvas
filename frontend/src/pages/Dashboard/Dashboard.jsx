import "./Dashboard.css";

import { Link } from "react-router-dom";

import {
  FaSearch,
  FaStar,
  FaRegHeart,
  FaRegComment,
  FaCodeBranch,
  FaRegBookmark,
  FaChartLine,
  FaArrowRight,
} from "react-icons/fa";

import heroImage from "../../assets/hero.png";

function Dashboard() {
  const snippets = [
    {
      id: 1,
      title: "useDebounce Hook",
      language: "React",
      code: `const result = await api.fetch(
  endpoint, { cache: true }
);`,
      likes: 342,
      comments: 28,
      forks: 102,
    },
    {
      id: 2,
      title: "Async Rate Limiter",
      language: "Python",
      code: `const result = await api.fetch(
  endpoint, { cache: true }
);`,
      likes: 289,
      comments: 14,
      forks: 86,
    },
    {
      id: 3,
      title: "HTTP Middleware Chain",
      language: "Go",
      code: `const result = await api.fetch(
  endpoint, { cache: true }
);`,
      likes: 284,
      comments: 19,
      forks: 61,
    },
  ];

  return (
    <div className="dashboardPage">
      {/* SEARCH SECTION */}

      <section className="searchSection">
        <div className="dashboardSearchBox">
          <FaSearch />

          <input
            type="text"
            placeholder="Search snippets, languages, developers..."
          />
        </div>

        <button type="button" className="filterButton">
          Filter
        </button>
      </section>

      {/* DASHBOARD CONTENT */}

      <main className="dashboardContent">
        {/* HERO SECTION */}

        <section className="heroSection" id="explore">
          <div className="heroContent">
            <p className="heroEyebrow">
              VISUAL CODE COLLABORATION PLATFORM
            </p>

            <h1>
              Share. Discover.
              <br />
              Collaborate on Code.
            </h1>

            <p className="heroDescription">
              Browse 240,000+ community snippets, fork and remix instantly,
              <br />
              collaborate in real time.
            </p>

            <div className="heroButtons">
              <Link to="/register" className="getStartedButton">
                Get Started
              </Link>

              <Link to="/search" className="browseButton">
                Browse Snippets
              </Link>
            </div>

            <div className="heroStats">
              <div>
                <strong>12k+</strong>
                <span>Developers</span>
              </div>

              <div>
                <strong>240k+</strong>
                <span>Snippets</span>
              </div>

              <div>
                <strong>98k+</strong>
                <span>Forks</span>
              </div>
            </div>
          </div>

          <div className="heroImageContainer">
            <img
              src={heroImage}
              alt="Developer coding on laptop"
            />

            <div className="heroImageOverlay"></div>

            <span className="heroImageLabel">
              HERO — CODE EDITOR COLLABORATION
            </span>
          </div>
        </section>

        {/* TRENDING SECTION */}

        <section className="trendingSection" id="trending">
          <div className="sectionHeader">
            <h2>
              <FaChartLine />
              TRENDING THIS WEEK
            </h2>

            <Link to="/search">
              View All
              <FaArrowRight />
            </Link>
          </div>

          <div className="snippetGrid">
            {snippets.map((snippet) => (
              <article
                className="snippetCard"
                key={snippet.id}
              >
                <div className="snippetHeader">
                  <div>
                    <h3>{snippet.title}</h3>

                    <span className="languageBadge">
                      {snippet.language}
                    </span>
                  </div>

                  <button
                    type="button"
                    className="starButton"
                    aria-label={`Star ${snippet.title}`}
                  >
                    <FaStar />
                  </button>
                </div>

                <pre className="codePreview">
                  <code>{snippet.code}</code>
                </pre>

                <div className="snippetFooter">
                  <div className="snippetStats">
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
                    className="bookmarkButton"
                    aria-label={`Bookmark ${snippet.title}`}
                  >
                    <FaRegBookmark />
                  </button>
                </div>
              </article>
            ))}
          </div>
        </section>
      </main>
    </div>
  );
}

export default Dashboard;