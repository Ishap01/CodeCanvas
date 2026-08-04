import React, {
    useCallback,
    useEffect,
    useMemo,
    useState,
} from "react";

import {
    Link,
    useNavigate,
    useSearchParams,
} from "react-router-dom";

import {
    FaBookmark,
    FaCode,
    FaFilter,
    FaPlus,
    FaRedoAlt,
    FaSearch,
    FaTimes,
} from "react-icons/fa";

import SnippetCard from "../../../components/snippets/SnippetCard/SnippetCard";

import {
    bookmarkSnippet,
    getPublicSnippets,
    getSnippetBookmarkStatus,
    removeSnippetBookmark,
} from "../../../services/snippetService";

import "./PublicSnippets.css";

const SORT_OPTIONS = [
    {
        value: "NEWEST",
        label: "Newest first",
    },
    {
        value: "OLDEST",
        label: "Oldest first",
    },
    {
        value: "MOST_VIEWED",
        label: "Most viewed",
    },
    {
        value: "MOST_LIKED",
        label: "Most liked",
    },
    {
        value: "MOST_BOOKMARKED",
        label: "Most bookmarked",
    },
];

function PublicSnippets() {

    const navigate = useNavigate();

    const [
        searchParams,
        setSearchParams,
    ] = useSearchParams();

    const token =
        localStorage.getItem("token");

    const querySearchValue =
        searchParams.get("search") || "";

    const [snippets, setSnippets] =
        useState([]);

    const [
        bookmarkStatus,
        setBookmarkStatus,
    ] = useState({});

    const [
        bookmarkLoading,
        setBookmarkLoading,
    ] = useState({});

    const [searchText, setSearchText] =
        useState(querySearchValue);

    const [
        selectedLanguage,
        setSelectedLanguage,
    ] = useState("ALL");

    const [
        selectedCategory,
        setSelectedCategory,
    ] = useState("ALL");

    const [sortOption, setSortOption] =
        useState("NEWEST");

    const [isLoading, setIsLoading] =
        useState(true);

    const [
        errorMessage,
        setErrorMessage,
    ] = useState("");

    const [
        actionMessage,
        setActionMessage,
    ] = useState("");

    const [
        showMobileFilters,
        setShowMobileFilters,
    ] = useState(false);

    /*
     * Navbar se URL query update hone par
     * local search input bhi update hoga.
     */
    useEffect(() => {
        setSearchText(querySearchValue);
    }, [querySearchValue]);

    const loadBookmarkStatuses =
        useCallback(
            async (snippetList) => {

                if (
                    !token ||
                    !Array.isArray(snippetList) ||
                    snippetList.length === 0
                ) {
                    setBookmarkStatus({});
                    return;
                }

                const statusEntries =
                    await Promise.all(
                        snippetList.map(
                            async (snippet) => {

                                try {
                                    const response =
                                        await getSnippetBookmarkStatus(
                                            snippet.snippetId
                                        );

                                    return [
                                        snippet.snippetId,
                                        Boolean(
                                            response?.bookmarked
                                        ),
                                    ];

                                } catch (error) {

                                    /*
                                     * Ek status API fail hone par
                                     * complete public page fail nahi hoga.
                                     */
                                    return [
                                        snippet.snippetId,
                                        false,
                                    ];
                                }
                            }
                        )
                    );

                setBookmarkStatus(
                    Object.fromEntries(
                        statusEntries
                    )
                );
            },
            [token]
        );

    const loadPublicSnippets =
        useCallback(async () => {

            try {
                setIsLoading(true);
                setErrorMessage("");

                const response =
                    await getPublicSnippets();

                const snippetList =
                    Array.isArray(response)
                        ? response
                        : [];

                setSnippets(snippetList);

                await loadBookmarkStatuses(
                    snippetList
                );

            } catch (error) {

                setSnippets([]);

                setErrorMessage(
                    error.message ||
                    "Unable to load public snippets."
                );

            } finally {
                setIsLoading(false);
            }

        }, [loadBookmarkStatuses]);

    useEffect(() => {
        loadPublicSnippets();
    }, [loadPublicSnippets]);

    const languages = useMemo(() => {

        const values = snippets
            .map((snippet) =>
                snippet.language?.trim()
            )
            .filter(Boolean);

        return [
            ...new Set(values),
        ].sort((first, second) =>
            first.localeCompare(second)
        );

    }, [snippets]);

    const categories = useMemo(() => {

        const values = snippets
            .map((snippet) =>
                snippet.categoryName?.trim()
            )
            .filter(Boolean);

        return [
            ...new Set(values),
        ].sort((first, second) =>
            first.localeCompare(second)
        );

    }, [snippets]);

    const filteredSnippets = useMemo(() => {

        const normalizedSearch =
            searchText
                .trim()
                .toLowerCase();

        const result = snippets.filter(
            (snippet) => {

                const matchesLanguage =
                    selectedLanguage === "ALL" ||
                    snippet.language ===
                    selectedLanguage;

                const matchesCategory =
                    selectedCategory === "ALL" ||
                    snippet.categoryName ===
                    selectedCategory;

                if (
                    !matchesLanguage ||
                    !matchesCategory
                ) {
                    return false;
                }

                if (!normalizedSearch) {
                    return true;
                }

                const searchableValues = [
                    snippet.title,
                    snippet.description,
                    snippet.language,
                    snippet.framework,
                    snippet.categoryName,
                    ...(snippet.tags || []),
                ];

                return searchableValues.some(
                    (value) =>
                        String(value || "")
                            .toLowerCase()
                            .includes(
                                normalizedSearch
                            )
                );
            }
        );

        const sortedResult =
            [...result];

        sortedResult.sort(
            (firstSnippet, secondSnippet) => {

                switch (sortOption) {

                    case "OLDEST":
                        return (
                            new Date(
                                firstSnippet.createdAt ||
                                0
                            ).getTime() -
                            new Date(
                                secondSnippet.createdAt ||
                                0
                            ).getTime()
                        );

                    case "MOST_VIEWED":
                        return (
                            Number(
                                secondSnippet.viewCount
                            ) -
                            Number(
                                firstSnippet.viewCount
                            )
                        );

                    case "MOST_LIKED":
                        return (
                            Number(
                                secondSnippet.likeCount
                            ) -
                            Number(
                                firstSnippet.likeCount
                            )
                        );

                    case "MOST_BOOKMARKED":
                        return (
                            Number(
                                secondSnippet.bookmarkCount
                            ) -
                            Number(
                                firstSnippet.bookmarkCount
                            )
                        );

                    case "NEWEST":
                    default:
                        return (
                            new Date(
                                secondSnippet.createdAt ||
                                0
                            ).getTime() -
                            new Date(
                                firstSnippet.createdAt ||
                                0
                            ).getTime()
                        );
                }
            }
        );

        return sortedResult;

    }, [
        snippets,
        searchText,
        selectedLanguage,
        selectedCategory,
        sortOption,
    ]);

    const handleSearchChange = (
        event
    ) => {

        const value =
            event.target.value;

        setSearchText(value);

        const updatedParams =
            new URLSearchParams(
                searchParams
            );

        if (value.trim()) {
            updatedParams.set(
                "search",
                value
            );
        } else {
            updatedParams.delete(
                "search"
            );
        }

        setSearchParams(
            updatedParams,
            {
                replace: true,
            }
        );
    };

    const clearSearch = () => {

        setSearchText("");

        const updatedParams =
            new URLSearchParams(
                searchParams
            );

        updatedParams.delete("search");

        setSearchParams(
            updatedParams,
            {
                replace: true,
            }
        );
    };

    const clearAllFilters = () => {

        setSearchText("");
        setSelectedLanguage("ALL");
        setSelectedCategory("ALL");
        setSortOption("NEWEST");

        const updatedParams =
            new URLSearchParams(
                searchParams
            );

        updatedParams.delete("search");

        setSearchParams(
            updatedParams,
            {
                replace: true,
            }
        );
    };

    const hasActiveFilters =
        Boolean(searchText.trim()) ||
        selectedLanguage !== "ALL" ||
        selectedCategory !== "ALL" ||
        sortOption !== "NEWEST";

    const handleBookmarkToggle =
        async (snippet) => {

            if (!token) {
                navigate(
                    "/login",
                    {
                        state: {
                            message:
                                "Please login to bookmark snippets.",
                            redirectTo:
                                `/snippets`,
                        },
                    }
                );

                return;
            }

            const snippetId =
                snippet?.snippetId;

            if (
                !snippetId ||
                bookmarkLoading[snippetId]
            ) {
                return;
            }

            const currentlyBookmarked =
                Boolean(
                    bookmarkStatus[snippetId]
                );

            try {
                setBookmarkLoading(
                    (previousState) => ({
                        ...previousState,
                        [snippetId]: true,
                    })
                );

                setActionMessage("");

                const response =
                    currentlyBookmarked
                        ? await removeSnippetBookmark(
                            snippetId
                        )
                        : await bookmarkSnippet(
                            snippetId
                        );

                const nextBookmarked =
                    Boolean(
                        response?.bookmarked
                    );

                setBookmarkStatus(
                    (previousState) => ({
                        ...previousState,
                        [snippetId]:
                            nextBookmarked,
                    })
                );

                setSnippets(
                    (previousSnippets) =>
                        previousSnippets.map(
                            (existingSnippet) => {

                                if (
                                    existingSnippet.snippetId !==
                                    snippetId
                                ) {
                                    return existingSnippet;
                                }

                                return {
                                    ...existingSnippet,

                                    bookmarkCount:
                                        Number(
                                            response?.bookmarkCount
                                        ) ||
                                        0,
                                };
                            }
                        )
                );

                setActionMessage(
                    response?.message ||
                    (
                        nextBookmarked
                            ? "Snippet bookmarked successfully."
                            : "Bookmark removed successfully."
                    )
                );

                window.setTimeout(() => {
                    setActionMessage("");
                }, 2500);

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to update bookmark."
                );

            } finally {

                setBookmarkLoading(
                    (previousState) => ({
                        ...previousState,
                        [snippetId]: false,
                    })
                );
            }
        };

    return (
        <main className="publicSnippetsPage">

            <section className="publicSnippetsHero">

                <div className="publicSnippetsHeroContent">

                    <p className="publicSnippetsEyebrow">
                        EXPLORE CODE
                    </p>

                    <h1>
                        Discover public snippets
                    </h1>

                    <p>
                        Explore reusable code,
                        programming solutions and
                        ideas shared by the
                        CodeCanvas community.
                    </p>

                    <div className="publicSnippetsHeroActions">

                        <Link
                            to="/snippets/create"
                            className="publicSnippetsCreateButton"
                        >
                            <FaPlus />

                            Create snippet
                        </Link>

                        <Link
                            to="/snippets/my"
                            className="publicSnippetsSecondaryButton"
                        >
                            <FaCode />

                            My snippets
                        </Link>

                    </div>

                </div>

                <div className="publicSnippetsHeroDecoration">

                    <span>
                        &lt;/&gt;
                    </span>

                </div>

            </section>

            <section className="publicSnippetsContent">

                {actionMessage && (
                    <div
                        className="publicSnippetsActionMessage"
                        role="status"
                    >
                        <FaBookmark />

                        {actionMessage}
                    </div>
                )}

                <div className="publicSnippetsToolbar">

                    <div className="publicSnippetsSearchWrapper">

                        <FaSearch />

                        <input
                            type="text"
                            value={searchText}
                            onChange={
                                handleSearchChange
                            }
                            placeholder="Search by title, language, framework, category or tag..."
                            aria-label="Search public snippets"
                        />

                        {searchText && (
                            <button
                                type="button"
                                onClick={
                                    clearSearch
                                }
                                aria-label="Clear search"
                            >
                                <FaTimes />
                            </button>
                        )}

                    </div>

                    <button
                        type="button"
                        className="publicSnippetsMobileFilterButton"
                        onClick={() =>
                            setShowMobileFilters(
                                (currentValue) =>
                                    !currentValue
                            )
                        }
                    >
                        <FaFilter />

                        Filters
                    </button>

                    <button
                        type="button"
                        className="publicSnippetsRefreshButton"
                        onClick={
                            loadPublicSnippets
                        }
                        disabled={isLoading}
                    >
                        <FaRedoAlt />

                        Refresh
                    </button>

                </div>

                <div
                    className={`publicSnippetsFilterPanel ${
                        showMobileFilters
                            ? "publicSnippetsFilterPanelVisible"
                            : ""
                    }`}
                >

                    <div className="publicSnippetsFilterField">

                        <label htmlFor="publicSnippetLanguage">
                            Language
                        </label>

                        <select
                            id="publicSnippetLanguage"
                            value={
                                selectedLanguage
                            }
                            onChange={(event) =>
                                setSelectedLanguage(
                                    event.target.value
                                )
                            }
                        >
                            <option value="ALL">
                                All languages
                            </option>

                            {languages.map(
                                (language) => (
                                    <option
                                        key={
                                            language
                                        }
                                        value={
                                            language
                                        }
                                    >
                                        {language}
                                    </option>
                                )
                            )}

                        </select>

                    </div>

                    <div className="publicSnippetsFilterField">

                        <label htmlFor="publicSnippetCategory">
                            Category
                        </label>

                        <select
                            id="publicSnippetCategory"
                            value={
                                selectedCategory
                            }
                            onChange={(event) =>
                                setSelectedCategory(
                                    event.target.value
                                )
                            }
                        >
                            <option value="ALL">
                                All categories
                            </option>

                            {categories.map(
                                (category) => (
                                    <option
                                        key={
                                            category
                                        }
                                        value={
                                            category
                                        }
                                    >
                                        {category}
                                    </option>
                                )
                            )}

                        </select>

                    </div>

                    <div className="publicSnippetsFilterField">

                        <label htmlFor="publicSnippetSort">
                            Sort by
                        </label>

                        <select
                            id="publicSnippetSort"
                            value={sortOption}
                            onChange={(event) =>
                                setSortOption(
                                    event.target.value
                                )
                            }
                        >
                            {SORT_OPTIONS.map(
                                (option) => (
                                    <option
                                        key={
                                            option.value
                                        }
                                        value={
                                            option.value
                                        }
                                    >
                                        {
                                            option.label
                                        }
                                    </option>
                                )
                            )}

                        </select>

                    </div>

                    {hasActiveFilters && (
                        <button
                            type="button"
                            className="publicSnippetsClearFiltersButton"
                            onClick={
                                clearAllFilters
                            }
                        >
                            <FaTimes />

                            Clear filters
                        </button>
                    )}

                </div>

                {!isLoading &&
                    !errorMessage && (
                    <div className="publicSnippetsResultHeader">

                        <div>
                            <h2>
                                Community snippets
                            </h2>

                            <p>
                                Showing{" "}
                                <strong>
                                    {
                                        filteredSnippets.length
                                    }
                                </strong>{" "}
                                of{" "}
                                <strong>
                                    {
                                        snippets.length
                                    }
                                </strong>{" "}
                                snippets
                            </p>
                        </div>

                        {hasActiveFilters && (
                            <span className="publicSnippetsFilteredBadge">
                                Filtered results
                            </span>
                        )}

                    </div>
                )}

                {errorMessage && (
                    <div className="publicSnippetsErrorState">

                        <span className="publicSnippetsStateIcon">
                            !
                        </span>

                        <h2>
                            Unable to load snippets
                        </h2>

                        <p>
                            {errorMessage}
                        </p>

                        <button
                            type="button"
                            onClick={
                                loadPublicSnippets
                            }
                        >
                            <FaRedoAlt />

                            Try again
                        </button>

                    </div>
                )}

                {isLoading && (
                    <div className="publicSnippetsGrid">

                        {Array.from({
                            length: 6,
                        }).map((_, index) => (
                            <div
                                key={index}
                                className="publicSnippetSkeleton"
                            >
                                <div className="publicSnippetSkeletonImage" />

                                <div className="publicSnippetSkeletonBody">
                                    <span />
                                    <span />
                                    <span />
                                    <span />
                                </div>
                            </div>
                        ))}

                    </div>
                )}

                {!isLoading &&
                    !errorMessage &&
                    filteredSnippets.length >
                    0 && (
                    <div className="publicSnippetsGrid">

                        {filteredSnippets.map(
                            (snippet) => (
                                <div
                                    key={
                                        snippet.snippetId
                                    }
                                    className="publicSnippetCardWrapper"
                                >
                                    <SnippetCard
                                        snippet={
                                            snippet
                                        }
                                        showBookmarkAction={
                                            true
                                        }
                                        isBookmarked={
                                            Boolean(
                                                bookmarkStatus[
                                                    snippet
                                                        .snippetId
                                                ]
                                            )
                                        }
                                        bookmarkLoading={
                                            Boolean(
                                                bookmarkLoading[
                                                    snippet
                                                        .snippetId
                                                ]
                                            )
                                        }
                                        onBookmarkToggle={
                                            handleBookmarkToggle
                                        }
                                    />

                                    {bookmarkLoading[
                                        snippet
                                            .snippetId
                                    ] && (
                                        <div className="publicSnippetBookmarkLoader">
                                            <span />
                                        </div>
                                    )}
                                </div>
                            )
                        )}

                    </div>
                )}

                {!isLoading &&
                    !errorMessage &&
                    filteredSnippets.length ===
                    0 && (
                    <div className="publicSnippetsEmptyState">

                        <span className="publicSnippetsStateIcon">
                            <FaSearch />
                        </span>

                        <h2>
                            No snippets found
                        </h2>

                        <p>
                            {snippets.length === 0
                                ? "No public snippets have been shared yet."
                                : "No snippets match your current search and filters."}
                        </p>

                        {hasActiveFilters ? (
                            <button
                                type="button"
                                onClick={
                                    clearAllFilters
                                }
                            >
                                <FaTimes />

                                Clear filters
                            </button>
                        ) : (
                            <Link
                                to="/snippets/create"
                            >
                                <FaPlus />

                                Create first snippet
                            </Link>
                        )}

                    </div>
                )}

            </section>

        </main>
    );
}

export default PublicSnippets;