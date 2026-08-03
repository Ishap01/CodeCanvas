import React, {
    useCallback,
    useEffect,
    useMemo,
    useState,
} from "react";

import {
    Link,
} from "react-router-dom";

import {
    FaBookmark,
    FaCode,
    FaExclamationTriangle,
    FaFilter,
    FaRedoAlt,
    FaSearch,
    FaTimes,
} from "react-icons/fa";

import SnippetCard from "../../../components/snippets/SnippetCard/SnippetCard";

import {
    getMyBookmarkedSnippets,
    removeSnippetBookmark,
} from "../../../services/snippetService";

import "./BookmarkedSnippets.css";

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
        value: "MOST_FORKED",
        label: "Most forked",
    },
];

function BookmarkedSnippets() {

    const [
        snippets,
        setSnippets,
    ] = useState([]);

    const [
        searchText,
        setSearchText,
    ] = useState("");

    const [
        selectedLanguage,
        setSelectedLanguage,
    ] = useState("ALL");

    const [
        sortOption,
        setSortOption,
    ] = useState("NEWEST");

    const [
        isLoading,
        setIsLoading,
    ] = useState(true);

    const [
        errorMessage,
        setErrorMessage,
    ] = useState("");

    const [
        successMessage,
        setSuccessMessage,
    ] = useState("");

    const [
        removingSnippetId,
        setRemovingSnippetId,
    ] = useState(null);

    const [
        showMobileFilters,
        setShowMobileFilters,
    ] = useState(false);

    const loadBookmarkedSnippets =
        useCallback(async () => {

            try {
                setIsLoading(true);
                setErrorMessage("");

                const response =
                    await getMyBookmarkedSnippets();

                setSnippets(
                    Array.isArray(response)
                        ? response
                        : []
                );

            } catch (error) {

                setSnippets([]);

                setErrorMessage(
                    error.message ||
                    "Unable to load bookmarked snippets."
                );

            } finally {
                setIsLoading(false);
            }

        }, []);

    useEffect(() => {
        loadBookmarkedSnippets();
    }, [loadBookmarkedSnippets]);

    const languages = useMemo(() => {

        const languageSet =
            new Set();

        snippets.forEach((snippet) => {

            const language =
                snippet?.language?.trim();

            if (language) {
                languageSet.add(language);
            }
        });

        return Array.from(
            languageSet
        ).sort((first, second) =>
            first.localeCompare(second)
        );

    }, [snippets]);

    const bookmarkStatistics =
        useMemo(() => {

            return snippets.reduce(
                (statistics, snippet) => {

                    statistics.total += 1;

                    statistics.views +=
                        Number(
                            snippet?.viewCount
                        ) || 0;

                    statistics.likes +=
                        Number(
                            snippet?.likeCount
                        ) || 0;

                    statistics.forks +=
                        Number(
                            snippet?.forkCount
                        ) || 0;

                    return statistics;
                },
                {
                    total: 0,
                    views: 0,
                    likes: 0,
                    forks: 0,
                }
            );

        }, [snippets]);

    const filteredSnippets =
        useMemo(() => {

            const normalizedSearch =
                searchText
                    .trim()
                    .toLowerCase();

            const matchingSnippets =
                snippets.filter(
                    (snippet) => {

                        const matchesLanguage =
                            selectedLanguage ===
                                "ALL" ||
                            snippet.language ===
                                selectedLanguage;

                        if (!matchesLanguage) {
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

            const sortedSnippets = [
                ...matchingSnippets,
            ];

            sortedSnippets.sort(
                (
                    firstSnippet,
                    secondSnippet
                ) => {

                    switch (sortOption) {

                        case "OLDEST":
                            return (
                                getTimeValue(
                                    firstSnippet
                                        .createdAt
                                ) -
                                getTimeValue(
                                    secondSnippet
                                        .createdAt
                                )
                            );

                        case "MOST_VIEWED":
                            return (
                                getNumberValue(
                                    secondSnippet
                                        .viewCount
                                ) -
                                getNumberValue(
                                    firstSnippet
                                        .viewCount
                                )
                            );

                        case "MOST_LIKED":
                            return (
                                getNumberValue(
                                    secondSnippet
                                        .likeCount
                                ) -
                                getNumberValue(
                                    firstSnippet
                                        .likeCount
                                )
                            );

                        case "MOST_FORKED":
                            return (
                                getNumberValue(
                                    secondSnippet
                                        .forkCount
                                ) -
                                getNumberValue(
                                    firstSnippet
                                        .forkCount
                                )
                            );

                        case "NEWEST":
                        default:
                            return (
                                getTimeValue(
                                    secondSnippet
                                        .createdAt
                                ) -
                                getTimeValue(
                                    firstSnippet
                                        .createdAt
                                )
                            );
                    }
                }
            );

            return sortedSnippets;

        }, [
            snippets,
            searchText,
            selectedLanguage,
            sortOption,
        ]);

    const hasActiveFilters =
        Boolean(searchText.trim()) ||
        selectedLanguage !== "ALL" ||
        sortOption !== "NEWEST";

    const clearFilters = () => {

        setSearchText("");
        setSelectedLanguage("ALL");
        setSortOption("NEWEST");
    };

    const showTemporarySuccess = (
        message
    ) => {

        setSuccessMessage(message);

        window.setTimeout(() => {
            setSuccessMessage("");
        }, 2800);
    };

    const handleRemoveBookmark =
        async (snippet) => {

            const snippetId =
                snippet?.snippetId;

            if (
                !snippetId ||
                removingSnippetId
            ) {
                return;
            }

            try {
                setRemovingSnippetId(
                    snippetId
                );

                setErrorMessage("");

                const response =
                    await removeSnippetBookmark(
                        snippetId
                    );

                setSnippets(
                    (previousSnippets) =>
                        previousSnippets.filter(
                            (existingSnippet) =>
                                existingSnippet
                                    .snippetId !==
                                snippetId
                        )
                );

                showTemporarySuccess(
                    response?.message ||
                    "Bookmark removed successfully."
                );

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to remove bookmark."
                );

            } finally {
                setRemovingSnippetId(
                    null
                );
            }
        };

    return (
        <main className="bookmarkedSnippetsPage">

            <section className="bookmarkedSnippetsHero">

                <div className="bookmarkedSnippetsHeroContent">

                    <p className="bookmarkedSnippetsEyebrow">
                        SAVED COLLECTION
                    </p>

                    <h1>
                        Bookmarked snippets
                    </h1>

                    <p>
                        Access the code snippets
                        you saved for future
                        reference and reuse.
                    </p>

                    <div className="bookmarkedSnippetsHeroActions">

                        <Link
                            to="/snippets"
                            className="bookmarkedSnippetsExploreButton"
                        >
                            <FaCode />

                            Explore snippets
                        </Link>

                        <Link
                            to="/snippets/my"
                            className="bookmarkedSnippetsSecondaryButton"
                        >
                            My snippets
                        </Link>

                    </div>

                </div>

                <div className="bookmarkedSnippetsHeroIcon">
                    <FaBookmark />
                </div>

            </section>

            <section className="bookmarkedSnippetsContent">

                {successMessage && (
                    <div
                        className="bookmarkedSnippetsAlert bookmarkedSnippetsSuccessAlert"
                        role="status"
                    >
                        <FaBookmark />

                        {successMessage}
                    </div>
                )}

                {errorMessage && (
                    <div
                        className="bookmarkedSnippetsAlert bookmarkedSnippetsErrorAlert"
                        role="alert"
                    >
                        <FaExclamationTriangle />

                        {errorMessage}
                    </div>
                )}

                {!isLoading && (
                    <div className="bookmarkedSnippetsStatistics">

                        <article>
                            <span>
                                Saved
                            </span>

                            <strong>
                                {
                                    bookmarkStatistics
                                        .total
                                }
                            </strong>

                            <small>
                                Bookmarked snippets
                            </small>
                        </article>

                        <article>
                            <span>
                                Views
                            </span>

                            <strong>
                                {formatCount(
                                    bookmarkStatistics
                                        .views
                                )}
                            </strong>

                            <small>
                                Combined views
                            </small>
                        </article>

                        <article>
                            <span>
                                Likes
                            </span>

                            <strong>
                                {formatCount(
                                    bookmarkStatistics
                                        .likes
                                )}
                            </strong>

                            <small>
                                Combined likes
                            </small>
                        </article>

                        <article>
                            <span>
                                Forks
                            </span>

                            <strong>
                                {formatCount(
                                    bookmarkStatistics
                                        .forks
                                )}
                            </strong>

                            <small>
                                Combined forks
                            </small>
                        </article>

                    </div>
                )}

                <div className="bookmarkedSnippetsToolbar">

                    <div className="bookmarkedSnippetsSearch">

                        <FaSearch />

                        <input
                            type="text"
                            value={searchText}
                            onChange={(event) =>
                                setSearchText(
                                    event.target.value
                                )
                            }
                            placeholder="Search bookmarked snippets..."
                            aria-label="Search bookmarked snippets"
                        />

                        {searchText && (
                            <button
                                type="button"
                                onClick={() =>
                                    setSearchText("")
                                }
                                aria-label="Clear search"
                            >
                                <FaTimes />
                            </button>
                        )}

                    </div>

                    <button
                        type="button"
                        className="bookmarkedSnippetsMobileFilterButton"
                        onClick={() =>
                            setShowMobileFilters(
                                (previousValue) =>
                                    !previousValue
                            )
                        }
                    >
                        <FaFilter />

                        Filters
                    </button>

                    <button
                        type="button"
                        className="bookmarkedSnippetsRefreshButton"
                        onClick={
                            loadBookmarkedSnippets
                        }
                        disabled={isLoading}
                    >
                        <FaRedoAlt />

                        Refresh
                    </button>

                </div>

                <div
                    className={`bookmarkedSnippetsFilters ${
                        showMobileFilters
                            ? "bookmarkedSnippetsFiltersVisible"
                            : ""
                    }`}
                >

                    <div className="bookmarkedSnippetsFilterField">

                        <label htmlFor="bookmarkLanguage">
                            Language
                        </label>

                        <select
                            id="bookmarkLanguage"
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
                                        key={language}
                                        value={language}
                                    >
                                        {language}
                                    </option>
                                )
                            )}

                        </select>

                    </div>

                    <div className="bookmarkedSnippetsFilterField">

                        <label htmlFor="bookmarkSort">
                            Sort by
                        </label>

                        <select
                            id="bookmarkSort"
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
                            className="bookmarkedSnippetsClearButton"
                            onClick={clearFilters}
                        >
                            <FaTimes />

                            Clear filters
                        </button>
                    )}

                </div>

                {!isLoading &&
                    !errorMessage && (
                    <div className="bookmarkedSnippetsResultHeader">

                        <div>
                            <h2>
                                Saved code collection
                            </h2>

                            <p>
                                Showing{" "}
                                <strong>
                                    {
                                        filteredSnippets
                                            .length
                                    }
                                </strong>{" "}
                                of{" "}
                                <strong>
                                    {
                                        snippets.length
                                    }
                                </strong>{" "}
                                saved snippets
                            </p>
                        </div>

                        {hasActiveFilters && (
                            <span>
                                Filtered results
                            </span>
                        )}

                    </div>
                )}

                {isLoading && (
                    <div className="bookmarkedSnippetsGrid">

                        {Array.from({
                            length: 6,
                        }).map((_, index) => (
                            <div
                                key={index}
                                className="bookmarkedSnippetSkeleton"
                            >
                                <div className="bookmarkedSnippetSkeletonImage" />

                                <div className="bookmarkedSnippetSkeletonBody">
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
                    errorMessage &&
                    snippets.length === 0 && (
                    <div className="bookmarkedSnippetsState">

                        <span className="bookmarkedSnippetsStateIcon bookmarkedSnippetsErrorIcon">
                            <FaExclamationTriangle />
                        </span>

                        <h2>
                            Unable to load bookmarks
                        </h2>

                        <p>
                            {errorMessage}
                        </p>

                        <button
                            type="button"
                            onClick={
                                loadBookmarkedSnippets
                            }
                        >
                            <FaRedoAlt />

                            Try again
                        </button>

                    </div>
                )}

                {!isLoading &&
                    filteredSnippets.length >
                        0 && (
                    <div className="bookmarkedSnippetsGrid">

                        {filteredSnippets.map(
                            (snippet) => (
                                <div
                                    key={
                                        snippet.snippetId
                                    }
                                    className="bookmarkedSnippetCardWrapper"
                                >
                                    <SnippetCard
                                        snippet={
                                            snippet
                                        }
                                        showBookmarkAction={
                                            true
                                        }
                                        isBookmarked={
                                            true
                                        }
                                        onBookmarkToggle={
                                            handleRemoveBookmark
                                        }
                                    />

                                    {removingSnippetId ===
                                        snippet.snippetId && (
                                        <div className="bookmarkedSnippetRemovingOverlay">
                                            <span />

                                            Removing...
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
                    <div className="bookmarkedSnippetsState">

                        <span className="bookmarkedSnippetsStateIcon">
                            <FaBookmark />
                        </span>

                        <h2>
                            {snippets.length === 0
                                ? "No bookmarked snippets yet"
                                : "No matching bookmarks found"}
                        </h2>

                        <p>
                            {snippets.length === 0
                                ? "Explore public snippets and bookmark useful code to access it here later."
                                : "Try changing or clearing the current search and filters."}
                        </p>

                        {snippets.length === 0 ? (
                            <Link to="/snippets">
                                <FaCode />

                                Explore snippets
                            </Link>
                        ) : (
                            <button
                                type="button"
                                onClick={clearFilters}
                            >
                                <FaTimes />

                                Clear filters
                            </button>
                        )}

                    </div>
                )}

            </section>

        </main>
    );
}

function getTimeValue(dateValue) {

    if (!dateValue) {
        return 0;
    }

    const date =
        new Date(dateValue);

    return Number.isNaN(
        date.getTime()
    )
        ? 0
        : date.getTime();
}

function getNumberValue(value) {
    return Number(value) || 0;
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

export default BookmarkedSnippets;