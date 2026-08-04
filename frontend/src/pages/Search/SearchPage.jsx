import React, {
    useEffect,
    useMemo,
    useState,
} from "react";

import {
    Link,
    useSearchParams,
} from "react-router-dom";

import {
    FaBookmark,
    FaCode,
    FaCodeBranch,
    FaEye,
    FaFire,
    FaHeart,
    FaSearch,
    FaTimes,
} from "react-icons/fa";

import "./SearchPage.css";

/*
 * Temporary frontend data.
 *
 * Search Service integrate karne ke baad
 * is array ko backend response se replace karenge.
 */
const DEMO_SNIPPETS = [
    {
        snippetId: "demo-1",
        title: "Spring Boot JWT Authentication",
        description:
            "Secure Spring Boot REST APIs using JWT authentication and authorization.",
        code:
            "@PostMapping(\"/login\")\npublic AuthResponse login(...) {\n    return authService.login(request);\n}",
        language: "Java",
        framework: "Spring Boot",
        categoryName: "Backend",
        visibility: "PUBLIC",
        likeCount: 128,
        bookmarkCount: 44,
        viewCount: 2100,
        forkCount: 19,
        tags: [
            "JWT",
            "Spring Security",
            "Backend",
        ],
        featured: true,
    },
    {
        snippetId: "demo-2",
        title: "React Custom Hook for API Calls",
        description:
            "Reusable React hook for handling loading, response and error states.",
        code:
            "const useApi = () => {\n    const [loading, setLoading] = useState(false);\n    return { loading };\n};",
        language: "JavaScript",
        framework: "React",
        categoryName: "Frontend",
        visibility: "PUBLIC",
        likeCount: 94,
        bookmarkCount: 38,
        viewCount: 1750,
        forkCount: 14,
        tags: [
            "React",
            "Hooks",
            "Axios",
        ],
        featured: true,
    },
    {
        snippetId: "demo-3",
        title: "Kafka Producer Configuration",
        description:
            "Production-style Kafka producer configuration for a Spring Boot microservice.",
        code:
            "@Bean\npublic ProducerFactory<String, Object> producerFactory() {\n    return new DefaultKafkaProducerFactory<>(config);\n}",
        language: "Java",
        framework: "Spring Kafka",
        categoryName: "Microservices",
        visibility: "PREMIUM",
        likeCount: 76,
        bookmarkCount: 31,
        viewCount: 1320,
        forkCount: 11,
        tags: [
            "Kafka",
            "Events",
            "Microservices",
        ],
        featured: false,
    },
    {
        snippetId: "demo-4",
        title: "Responsive CSS Grid Layout",
        description:
            "Responsive card grid with automatic column sizing and mobile breakpoints.",
        code:
            ".grid {\n    display: grid;\n    grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));\n}",
        language: "CSS",
        framework: "CSS Grid",
        categoryName: "UI Design",
        visibility: "PUBLIC",
        likeCount: 163,
        bookmarkCount: 67,
        viewCount: 3400,
        forkCount: 23,
        tags: [
            "CSS",
            "Responsive",
            "Grid",
        ],
        featured: true,
    },
    {
        snippetId: "demo-5",
        title: "PostgreSQL User Search Query",
        description:
            "Case-insensitive PostgreSQL user search with pagination support.",
        code:
            "SELECT *\nFROM users\nWHERE LOWER(username) LIKE LOWER(:query)\nLIMIT :limit;",
        language: "SQL",
        framework: "PostgreSQL",
        categoryName: "Database",
        visibility: "PUBLIC",
        likeCount: 54,
        bookmarkCount: 22,
        viewCount: 890,
        forkCount: 7,
        tags: [
            "PostgreSQL",
            "Search",
            "Database",
        ],
        featured: false,
    },
    {
        snippetId: "demo-6",
        title: "Python Audio Feature Extraction",
        description:
            "Extract MFCC audio features for machine-learning based audio classification.",
        code:
            "mfcc = librosa.feature.mfcc(\n    y=audio,\n    sr=sample_rate,\n    n_mfcc=40\n)",
        language: "Python",
        framework: "Librosa",
        categoryName: "Machine Learning",
        visibility: "PREMIUM",
        likeCount: 112,
        bookmarkCount: 56,
        viewCount: 2450,
        forkCount: 16,
        tags: [
            "Python",
            "Audio",
            "Machine Learning",
        ],
        featured: true,
    },
    {
        snippetId: "demo-7",
        title: "Axios JWT Request Interceptor",
        description:
            "Automatically attach a stored Bearer token to protected frontend requests.",
        code:
            "axiosInstance.interceptors.request.use((config) => {\n    config.headers.Authorization = `Bearer ${token}`;\n    return config;\n});",
        language: "JavaScript",
        framework: "Axios",
        categoryName: "Frontend",
        visibility: "PUBLIC",
        likeCount: 88,
        bookmarkCount: 41,
        viewCount: 1540,
        forkCount: 12,
        tags: [
            "Axios",
            "JWT",
            "React",
        ],
        featured: false,
    },
    {
        snippetId: "demo-8",
        title: "Razorpay Order Creation",
        description:
            "Create a Razorpay payment order using Spring Boot and Razorpay Java SDK.",
        code:
            "JSONObject options = new JSONObject();\noptions.put(\"amount\", amountInPaise);\nreturn razorpayClient.orders.create(options);",
        language: "Java",
        framework: "Razorpay",
        categoryName: "Payments",
        visibility: "PREMIUM",
        likeCount: 69,
        bookmarkCount: 29,
        viewCount: 1200,
        forkCount: 9,
        tags: [
            "Razorpay",
            "Payment",
            "Spring Boot",
        ],
        featured: false,
    },
];

const SEARCH_CATEGORIES = [
    "All",
    "Java",
    "JavaScript",
    "React",
    "Spring Boot",
    "Python",
    "CSS",
    "SQL",
    "Kafka",
];

const TRENDING_SEARCHES = [
    "Spring Boot JWT",
    "React Hooks",
    "Kafka Microservices",
    "Responsive CSS",
    "Razorpay Payment",
];

export default function SearchPage() {

    const [
        searchParams,
        setSearchParams,
    ] = useSearchParams();

    const queryFromUrl =
        searchParams.get("q") || "";

    const [searchText, setSearchText] =
        useState(queryFromUrl);

    const [
        selectedCategory,
        setSelectedCategory,
    ] = useState("All");

    useEffect(() => {
        setSearchText(queryFromUrl);
    }, [queryFromUrl]);

    const filteredSnippets =
        useMemo(() => {

            const normalizedSearch =
                queryFromUrl
                    .trim()
                    .toLowerCase();

            return DEMO_SNIPPETS.filter(
                (snippet) => {

                    const matchesCategory =
                        selectedCategory ===
                            "All" ||
                        snippet.language ===
                            selectedCategory ||
                        snippet.framework ===
                            selectedCategory ||
                        snippet.tags.some(
                            (tag) =>
                                tag.toLowerCase() ===
                                selectedCategory
                                    .toLowerCase()
                        );

                    if (!matchesCategory) {
                        return false;
                    }

                    if (!normalizedSearch) {
                        return true;
                    }

                    const searchableContent = [
                        snippet.title,
                        snippet.description,
                        snippet.language,
                        snippet.framework,
                        snippet.categoryName,
                        snippet.visibility,
                        ...snippet.tags,
                    ]
                        .join(" ")
                        .toLowerCase();

                    return searchableContent.includes(
                        normalizedSearch
                    );
                }
            );

        }, [
            queryFromUrl,
            selectedCategory,
        ]);

    const handleSearchSubmit = (event) => {

        event.preventDefault();

        const normalizedSearch =
            searchText.trim();

        if (!normalizedSearch) {
            setSearchParams({});
            return;
        }

        setSearchParams({
            q: normalizedSearch,
        });
    };

    const handleTrendingSearch =
        (searchValue) => {

            setSearchText(searchValue);

            setSearchParams({
                q: searchValue,
            });
        };

    const clearSearch = () => {

        setSearchText("");
        setSearchParams({});
    };

    return (
        <main className="searchPage">

            {/* ================= HERO ================= */}

            <section className="searchHero">

                <div className="searchHeroGlow" />

                <div className="searchHeroContent">

                    <p className="searchEyebrow">
                        <FaFire />

                        DISCOVER CODE
                    </p>

                    <h1>
                        Explore the developer
                        community.
                    </h1>

                    <p className="searchHeroDescription">
                        Search snippets, languages,
                        frameworks and reusable ideas
                        shared by CodeCanvas developers.
                    </p>

                    <form
                        className="searchPageForm"
                        onSubmit={
                            handleSearchSubmit
                        }
                    >
                        <FaSearch />

                        <input
                            type="text"
                            value={searchText}
                            onChange={(event) =>
                                setSearchText(
                                    event.target.value
                                )
                            }
                            placeholder="Search Java, React, Kafka, CSS..."
                            autoComplete="off"
                            aria-label="Search snippets"
                        />

                        {searchText && (
                            <button
                                type="button"
                                className="searchClearButton"
                                onClick={clearSearch}
                                aria-label="Clear search"
                            >
                                <FaTimes />
                            </button>
                        )}

                        <button
                            type="submit"
                            className="searchSubmitButton"
                        >
                            Search
                        </button>

                    </form>

                    <div className="trendingSearches">

                        <span>
                            Trending:
                        </span>

                        {TRENDING_SEARCHES.map(
                            (searchValue) => (
                                <button
                                    key={
                                        searchValue
                                    }
                                    type="button"
                                    onClick={() =>
                                        handleTrendingSearch(
                                            searchValue
                                        )
                                    }
                                >
                                    {searchValue}
                                </button>
                            )
                        )}

                    </div>

                </div>

                <div className="searchHeroVisual">

                    <div className="searchVisualWindow">

                        <div className="searchVisualHeader">
                            <span />
                            <span />
                            <span />
                        </div>

                        <div className="searchVisualCode">
                            <p>
                                <span>const</span>{" "}
                                explore ={" "}
                                <strong>
                                    community
                                </strong>
                                ;
                            </p>

                            <p>
                                <span>const</span>{" "}
                                result ={" "}
                                <strong>
                                    search
                                </strong>
                                (idea);
                            </p>

                            <p>
                                <em>
                                    return
                                </em>{" "}
                                result;
                            </p>
                        </div>

                    </div>

                </div>

            </section>

            {/* ================= CONTENT ================= */}

            <section className="searchPageContent">

                <div className="searchCategoryBar">

                    {SEARCH_CATEGORIES.map(
                        (category) => (
                            <button
                                key={category}
                                type="button"
                                className={
                                    selectedCategory ===
                                    category
                                        ? "activeSearchCategory"
                                        : ""
                                }
                                onClick={() =>
                                    setSelectedCategory(
                                        category
                                    )
                                }
                            >
                                {category}
                            </button>
                        )
                    )}

                </div>

                <div className="searchResultHeading">

                    <div>

                        <p>
                            {queryFromUrl
                                ? "SEARCH RESULTS"
                                : "EXPLORE"}
                        </p>

                        <h2>
                            {queryFromUrl
                                ? `Results for “${queryFromUrl}”`
                                : "Popular snippets"}
                        </h2>

                    </div>

                    <span>
                        {filteredSnippets.length}{" "}
                        {filteredSnippets.length === 1
                            ? "result"
                            : "results"}
                    </span>

                </div>

                {filteredSnippets.length > 0 ? (

                    <div className="searchExploreGrid">

                        {filteredSnippets.map(
                            (
                                snippet,
                                index
                            ) => (
                                <article
                                    key={
                                        snippet.snippetId
                                    }
                                    className={`searchExploreCard ${
                                        snippet.featured
                                            ? "searchFeaturedCard"
                                            : ""
                                    } ${
                                        index === 0 &&
                                        !queryFromUrl
                                            ? "searchLargeCard"
                                            : ""
                                    }`}
                                >

                                    <Link
                                        to={`/snippets/${snippet.snippetId}`}
                                        className="searchCardMainLink"
                                    >

                                        <div className="searchCardTop">

                                            <span className="searchCardLanguage">
                                                {
                                                    snippet.language
                                                }
                                            </span>

                                            <span
                                                className={`searchVisibilityBadge searchVisibility${snippet.visibility}`}
                                            >
                                                {
                                                    snippet.visibility
                                                }
                                            </span>

                                        </div>

                                        <div className="searchCardCode">

                                            <div className="searchCardCodeHeader">

                                                <span />
                                                <span />
                                                <span />

                                                <small>
                                                    {
                                                        snippet.framework
                                                    }
                                                </small>

                                            </div>

                                            <pre>
                                                <code>
                                                    {
                                                        snippet.code
                                                    }
                                                </code>
                                            </pre>

                                        </div>

                                        <div className="searchCardBody">

                                            <p className="searchCardCategory">
                                                {
                                                    snippet.categoryName
                                                }
                                            </p>

                                            <h3>
                                                {
                                                    snippet.title
                                                }
                                            </h3>

                                            <p className="searchCardDescription">
                                                {
                                                    snippet.description
                                                }
                                            </p>

                                            <div className="searchCardTags">

                                                {snippet.tags.map(
                                                    (
                                                        tag
                                                    ) => (
                                                        <span
                                                            key={
                                                                tag
                                                            }
                                                        >
                                                            #
                                                            {
                                                                tag
                                                            }
                                                        </span>
                                                    )
                                                )}

                                            </div>

                                        </div>

                                    </Link>

                                    <div className="searchCardFooter">

                                        <div className="searchCardStats">

                                            <span>
                                                <FaHeart />

                                                {formatCount(
                                                    snippet.likeCount
                                                )}
                                            </span>

                                            <span>
                                                <FaEye />

                                                {formatCount(
                                                    snippet.viewCount
                                                )}
                                            </span>

                                            <span>
                                                <FaCodeBranch />

                                                {formatCount(
                                                    snippet.forkCount
                                                )}
                                            </span>

                                        </div>

                                        <button
                                            type="button"
                                            aria-label={`Bookmark ${snippet.title}`}
                                        >
                                            <FaBookmark />

                                            {formatCount(
                                                snippet.bookmarkCount
                                            )}
                                        </button>

                                    </div>

                                </article>
                            )
                        )}

                    </div>

                ) : (

                    <div className="searchEmptyState">

                        <span>
                            <FaSearch />
                        </span>

                        <h2>
                            No snippets found
                        </h2>

                        <p>
                            Try another keyword or
                            select a different
                            category.
                        </p>

                        <button
                            type="button"
                            onClick={() => {
                                setSelectedCategory(
                                    "All"
                                );

                                clearSearch();
                            }}
                        >
                            <FaCode />

                            Explore all snippets
                        </button>

                    </div>

                )}

            </section>

        </main>
    );
}

function formatCount(value) {

    const number =
        Number(value) || 0;

    if (number >= 1000000) {
        return `${(
            number / 1000000
        ).toFixed(1)}M`;
    }

    if (number >= 1000) {
        return `${(
            number / 1000
        ).toFixed(1)}K`;
    }

    return number.toString();
}