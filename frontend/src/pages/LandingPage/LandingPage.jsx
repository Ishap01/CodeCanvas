import {
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  Link,
  useNavigate,
} from "react-router-dom";

import {
  FaArrowRight,
  FaCode,
  FaCodeBranch,
  FaCrown,
  FaLayerGroup,
  FaSearch,
  FaUsers,
} from "react-icons/fa";

import SnippetCard from "../../components/snippets/SnippetCard/SnippetCard";

import {
  getPublicSnippets,
} from "../../services/snippetService";

import heroImage from "../../assets/images/hero.png";

import "./LandingPage.css";

const LANDING_SNIPPET_LIMIT = 6;

const CATEGORIES = [
  {
    label: "Java",
    value: "Java",
  },
  {
    label: "JavaScript",
    value: "JavaScript",
  },
  {
    label: "React",
    value: "React",
  },
  {
    label: "Spring Boot",
    value: "Spring Boot",
  },
  {
    label: "Python",
    value: "Python",
  },
  {
    label: "Kafka",
    value: "Kafka",
  },
];

function unwrapResponseData(response) {
  if (response == null) {
    return {};
  }

  if (
    typeof response === "object" &&
    response.data !== undefined
  ) {
    return response.data;
  }

  return response;
}

function normalizeSnippet(snippet) {
  return {
    ...snippet,

    snippetId:
      snippet?.snippetId ||
      snippet?.id,

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
      snippet?.visibility ||
      "PUBLIC",

    tags:
      Array.isArray(snippet?.tags)
        ? snippet.tags
        : [],

    previewImageUrl:
      snippet?.previewImageUrl ||
      snippet?.imageUrl ||
      null,

    likeCount:
      Number(
        snippet?.likeCount ??
        snippet?.likes
      ) || 0,

    commentCount:
      Number(
        snippet?.commentCount ??
        snippet?.comments
      ) || 0,

    bookmarkCount:
      Number(
        snippet?.bookmarkCount ??
        snippet?.bookmarks
      ) || 0,

    viewCount:
      Number(
        snippet?.viewCount ??
        snippet?.views
      ) || 0,

    forkCount:
      Number(
        snippet?.forkCount ??
        snippet?.forks
      ) || 0,

    createdAt:
      snippet?.createdAt ||
      null,
  };
}

function LandingPage() {
  const navigate = useNavigate();

  const [searchText, setSearchText] =
    useState("");

  const [snippets, setSnippets] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [errorMessage, setErrorMessage] =
    useState("");

  const featuredSnippets =
    useMemo(() => {
      return snippets.slice(
        0,
        LANDING_SNIPPET_LIMIT
      );
    }, [snippets]);

  const loadPublicSnippets =
    async () => {
      try {
        setLoading(true);
        setErrorMessage("");

        const response =
          await getPublicSnippets();

        const payload =
          unwrapResponseData(response);

        const rawSnippets =
          Array.isArray(payload)
            ? payload
            : Array.isArray(
              payload?.snippets
            )
              ? payload.snippets
              : Array.isArray(
                payload?.content
              )
                ? payload.content
                : [];

        const normalizedSnippets =
          rawSnippets
            .map(normalizeSnippet)
            .filter(
              (snippet) =>
                Boolean(
                  snippet.snippetId
                )
            );

        setSnippets(
          normalizedSnippets
        );
      } catch (error) {
        console.error(
          "Landing snippets error:",
          error
        );

        setSnippets([]);

        setErrorMessage(
          error?.message ||
          "Unable to load public snippets."
        );
      } finally {
        setLoading(false);
      }
    };

  useEffect(() => {
    loadPublicSnippets();
  }, []);

  /*
   * Guest user ko login page par bhejega.
   *
   * Successful login ke baad redirectTo
   * wala original page open hoga.
   */
  const navigateWithLogin = (
    destination,
    message =
      "Please login to continue."
  ) => {
    if (token) {
      navigate(destination);
      return;
    }

    navigate(
      "/login",
      {
        state: {
          message,
          redirectTo:
            destination,
        },
      }
    );
  };

  /*
   * Search page ko guest ke liye
   * directly open nahi hone dega.
   */
  const executeSearch = (
    value
  ) => {
    const keyword =
      String(value || "").trim();

    const destination =
      keyword
        ? `/search?q=${encodeURIComponent(
          keyword
        )}`
        : "/search";

    navigateWithLogin(
      destination,
      "Please login to search snippets and developers."
    );
  };

  const handleSearchSubmit = (
    event
  ) => {
    event.preventDefault();

    executeSearch(searchText);
  };

  /*
   * Category click bhi protected search
   * page par jayega.
   */
  const handleCategorySearch = (
    category
  ) => {
    const normalizedCategory =
      String(category || "").trim();

    if (!normalizedCategory) {
      return;
    }

    setSearchText(
      normalizedCategory
    );

    const destination =
      `/search?q=${encodeURIComponent(
        normalizedCategory
      )}`;

    navigateWithLogin(
      destination,
      "Please login to explore community snippets."
    );
  };

  const handleBrowseSnippets =
    () => {
      navigateWithLogin(
        "/snippets",
        "Please login to browse public snippets."
      );
    };

  const handleSearchPage =
    () => {
      navigateWithLogin(
        "/search",
        "Please login to search snippets and developers."
      );
    };

  const handlePremiumPage =
    () => {
      navigateWithLogin(
        "/premium",
        "Please login to explore CodeCanvas Premium."
      );
    };

  return (
    <main className="landingPage">

      {/* ================= NAVBAR ================= */}

      <header className="landingNavbar">

        <Link
          to="/"
          className="landingBrand"
        >
          <span className="landingBrandIcon">
            <FaCode />
          </span>

          <span>
            CodeCanvas
          </span>
        </Link>

        <nav className="landingNavigation">

          <a href="#featured">
            Snippets
          </a>

          <a href="#features">
            Features
          </a>

          <button
            type="button"
            className="landingNavigationButton"
            onClick={
              handleSearchPage
            }
          >
            Search
          </button>

          <button
            type="button"
            className="landingNavigationButton"
            onClick={
              handlePremiumPage
            }
          >
            Premium
          </button>

        </nav>

        <div className="landingAuthActions">
          <Link
            to="/login"
            className="landingLoginButton"
          >
            Login
          </Link>

          <Link
            to="/register"
            className="landingPrimaryNavButton"
          >
            Join free
          </Link>
        </div>

      </header>

      {/* ================= HERO ================= */}

      <section className="landingHero">

        <div className="landingHeroGlow landingHeroGlowOne" />

        <div className="landingHeroGlow landingHeroGlowTwo" />

        <div className="landingHeroContent">

          <div className="landingHeroBadge">

            <FaLayerGroup />

            Visual code collaboration

          </div>

          <h1>
            Discover code.
            <br />

            <span>
              Build faster together.
            </span>
          </h1>

          <p>
            Explore reusable code snippets,
            learn from developers and share
            solutions with the CodeCanvas
            community.
          </p>

          <form
            className="landingSearchForm"
            onSubmit={
              handleSearchSubmit
            }
          >

            <FaSearch />

            <input
              type="text"
              value={searchText}
              placeholder="Search Java, React, Spring Boot, Kafka..."
              onChange={(event) =>
                setSearchText(
                  event.target.value
                )
              }
            />

            <button type="submit">
              Search
            </button>

          </form>

          <div className="landingPopularSearches">

            <span>
              Popular:
            </span>

            {CATEGORIES
              .slice(0, 4)
              .map((category) => (
                <button
                  key={
                    category.value
                  }
                  type="button"
                  onClick={() =>
                    handleCategorySearch(
                      category.value
                    )
                  }
                >
                  {category.label}
                </button>
              ))}

          </div>

          <div className="landingHeroButtons">

            <Link
              to="/register"
              className="landingHeroPrimaryButton"
            >
              Start building
              <FaArrowRight />
            </Link>

            <button
              type="button"
              className="landingHeroSecondaryButton"
              onClick={
                handleBrowseSnippets
              }
            >
              Browse snippets
            </button>

          </div>

          <div className="landingHeroStats">

            <div>
              <strong>
                Community
              </strong>

              <span>
                Developer-driven content
              </span>
            </div>

            <div>
              <strong>
                Reusable
              </strong>

              <span>
                Practical code solutions
              </span>
            </div>

            <div>
              <strong>
                Collaborative
              </strong>

              <span>
                Like, comment and fork
              </span>
            </div>

          </div>

        </div>

        <div className="landingHeroVisual">

          <div className="landingHeroImageCard">

            <img
              src={heroImage}
              alt="CodeCanvas developer collaboration"
            />

            <div className="landingHeroImageOverlay" />

            <div className="landingFloatingCodeCard landingFloatingCodeTop">

              <span>
                Java
              </span>

              <code>
                public class CodeCanvas
              </code>

            </div>

            <div className="landingFloatingCodeCard landingFloatingCodeBottom">

              <span>
                React
              </span>

              <code>
                const build = collaborate();
              </code>

            </div>

          </div>

        </div>

      </section>

      {/* ================= CATEGORIES ================= */}

      <section className="landingCategories">

        <div className="landingSectionHeading">

          <div>
            <p>
              EXPLORE TOPICS
            </p>

            <h2>
              Find code by technology
            </h2>
          </div>

          <button
            type="button"
            className="landingSectionLinkButton"
            onClick={
              handleSearchPage
            }
          >
            Explore search

            <FaArrowRight />
          </button>

        </div>

        <div className="landingCategoryGrid">

          {CATEGORIES.map(
            (category) => (
              <button
                key={
                  category.value
                }
                type="button"
                onClick={() =>
                  handleCategorySearch(
                    category.value
                  )
                }
              >
                <span>
                  <FaCode />
                </span>

                <strong>
                  {category.label}
                </strong>

                <small>
                  Explore snippets
                </small>
              </button>
            )
          )}

        </div>

      </section>

      {/* ================= FEATURED SNIPPETS ================= */}

      <section
        className="landingFeaturedSection"
        id="featured"
      >

        <div className="landingSectionHeading">

          <div>
            <p>
              COMMUNITY CODE
            </p>

            <h2>
              Featured public snippets
            </h2>

            <span>
              Explore real code shared by
              CodeCanvas developers.
            </span>
          </div>

          <button
            type="button"
            className="landingSectionLinkButton"
            onClick={
              handleBrowseSnippets
            }
          >
            View all snippets

            <FaArrowRight />
          </button>

        </div>

        {loading && (
          <div className="landingSnippetGrid">

            {Array.from({
              length: 6,
            }).map((_, index) => (
              <div
                key={index}
                className="landingSnippetSkeleton"
              >
                <div />
                <span />
                <span />
                <span />
              </div>
            ))}

          </div>
        )}

        {!loading &&
          errorMessage && (
            <div className="landingSnippetState">

              <span>
                !
              </span>

              <h3>
                Unable to load snippets
              </h3>

              <p>
                {errorMessage}
              </p>

              <button
                type="button"
                onClick={
                  loadPublicSnippets
                }
              >
                Try again
              </button>

            </div>
          )}

        {!loading &&
          !errorMessage &&
          featuredSnippets.length ===
          0 && (
            <div className="landingSnippetState">

              <FaCode />

              <h3>
                No public snippets yet
              </h3>

              <p>
                Be the first developer to
                share a public snippet.
              </p>

              <Link to="/register">
  Start building
</Link>

            </div>
          )}

        {!loading &&
          !errorMessage &&
          featuredSnippets.length >
          0 && (
            <div className="landingSnippetGrid">

              {featuredSnippets.map(
                (snippet) => (
                  <SnippetCard
                    key={
                      snippet.snippetId
                    }
                    snippet={snippet}
                    showOwnerActions={
                      false
                    }
                    showBookmarkAction={
                      false
                    }
                    requireLoginOnOpen={
                      true
                    }
                  />
                )
              )}

            </div>
          )}

      </section>

      {/* ================= FEATURES ================= */}

      <section
        className="landingFeaturesSection"
        id="features"
      >

        <div className="landingSectionHeading landingCenteredHeading">

          <div>
            <p>
              WHY CODECANVAS
            </p>

            <h2>
              Everything your code community needs
            </h2>

            <span>
              Learn, contribute and collaborate
              without losing context.
            </span>
          </div>

        </div>

        <div className="landingFeatureGrid">

          <article>

            <span>
              <FaCode />
            </span>

            <h3>
              Share snippets
            </h3>

            <p>
              Publish reusable code with
              language, framework, tags and
              visual previews.
            </p>

          </article>

          <article>

            <span>
              <FaUsers />
            </span>

            <h3>
              Discover developers
            </h3>

            <p>
              Search developers by full name,
              username or profile expertise.
            </p>

          </article>

          <article>

            <span>
              <FaCodeBranch />
            </span>

            <h3>
              Fork and improve
            </h3>

            <p>
              Reuse community solutions and
              evolve them for your own
              projects.
            </p>

          </article>

          <article>

            <span>
              <FaCrown />
            </span>

            <h3>
              Premium knowledge
            </h3>

            <p>
              Unlock advanced snippets and
              premium development resources.
            </p>

          </article>

        </div>

      </section>

      {/* ================= PREMIUM CTA ================= */}

      <section className="landingPremiumSection">

        <div>

          <span className="landingPremiumIcon">
            <FaCrown />
          </span>

          <div>
            <p>
              CODECANVAS PREMIUM
            </p>

            <h2>
              Unlock deeper code knowledge
            </h2>

            <span>
              Access premium snippets,
              advanced solutions and
              member-only features.
            </span>
          </div>

        </div>

        <button
          type="button"
          className="landingPremiumButton"
          onClick={
            handlePremiumPage
          }
        >
          Explore Premium

          <FaArrowRight />
        </button>

      </section>

      {/* ================= FOOTER ================= */}

      <footer className="landingFooter">

        <div>

          <Link
            to="/"
            className="landingFooterBrand"
          >
            <FaCode />

            CodeCanvas
          </Link>

          <p>
            A collaborative platform for
            sharing, discovering and improving
            code.
          </p>

        </div>

        <div className="landingFooterLinks">

          <button
            type="button"
            onClick={
              handleBrowseSnippets
            }
          >
            Snippets
          </button>

          <button
            type="button"
            onClick={
              handleSearchPage
            }
          >
            Search
          </button>

          <button
            type="button"
            onClick={
              handlePremiumPage
            }
          >
            Premium
          </button>

          <Link to="/login">
            Login
          </Link>

        </div>

        <span>
          © 2026 CodeCanvas
        </span>

      </footer>

    </main>
  );
}

export default LandingPage;