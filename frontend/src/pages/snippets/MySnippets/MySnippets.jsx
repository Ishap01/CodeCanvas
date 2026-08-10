import React, {
    useCallback,
    useEffect,
    useMemo,
    useState,
} from "react";

import {
    Link,
    useNavigate,
} from "react-router-dom";

import {
    FaCode,
    FaExclamationTriangle,
    FaFilter,
    FaPlus,
    FaRedoAlt,
    FaSearch,
    FaTimes,
    FaTrash,
} from "react-icons/fa";

import SnippetCard from "../../../components/snippets/SnippetCard/SnippetCard";

import {
    deleteSnippet,
    getMySnippets,
} from "../../../services/snippetService";

import "./MySnippets.css";

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

const VISIBILITY_OPTIONS = [
    {
        value: "ALL",
        label: "All visibility",
    },
    {
        value: "PUBLIC",
        label: "Public",
    },
    {
        value: "PREMIUM",
        label: "Premium",
    },
    {
        value: "PRIVATE",
        label: "Private",
    },
];

function MySnippets() {

    const navigate = useNavigate();

    const [snippets, setSnippets] =
        useState([]);

    const [searchText, setSearchText] =
        useState("");

    const [
        selectedVisibility,
        setSelectedVisibility,
    ] = useState("ALL");

    const [
        selectedLanguage,
        setSelectedLanguage,
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
        successMessage,
        setSuccessMessage,
    ] = useState("");

    const [
        deleteCandidate,
        setDeleteCandidate,
    ] = useState(null);

    const [
        deletingSnippetId,
        setDeletingSnippetId,
    ] = useState(null);

    const [
        showMobileFilters,
        setShowMobileFilters,
    ] = useState(false);

    const loadMySnippets =
        useCallback(async () => {

            try {
                setIsLoading(true);
                setErrorMessage("");

                const response =
                    await getMySnippets();

                setSnippets(
                    Array.isArray(response)
                        ? response
                        : []
                );

            } catch (error) {

                setSnippets([]);

                setErrorMessage(
                    error.message ||
                    "Unable to load your snippets."
                );

            } finally {
                setIsLoading(false);
            }

        }, []);

    useEffect(() => {
        loadMySnippets();
    }, [loadMySnippets]);

    const languages = useMemo(() => {

        const uniqueLanguages =
            new Set();

        snippets.forEach((snippet) => {

            const language =
                snippet?.language?.trim();

            if (language) {
                uniqueLanguages.add(language);
            }
        });

        return Array.from(
            uniqueLanguages
        ).sort((first, second) =>
            first.localeCompare(second)
        );

    }, [snippets]);

    const snippetStatistics =
        useMemo(() => {

            return snippets.reduce(
                (statistics, snippet) => {

                    statistics.total += 1;

                    switch (
                        snippet.visibility
                    ) {
                        case "PUBLIC":
                            statistics.public += 1;
                            break;

                        case "PREMIUM":
                            statistics.premium += 1;
                            break;

                        case "PRIVATE":
                            statistics.private += 1;
                            break;

                        default:
                            break;
                    }

                    statistics.views +=
                        Number(
                            snippet.viewCount
                        ) || 0;

                    statistics.likes +=
                        Number(
                            snippet.likeCount
                        ) || 0;

                    return statistics;
                },
                {
                    total: 0,
                    public: 0,
                    premium: 0,
                    private: 0,
                    views: 0,
                    likes: 0,
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

                        const matchesVisibility =
                            selectedVisibility ===
                                "ALL" ||
                            snippet.visibility ===
                                selectedVisibility;

                        const matchesLanguage =
                            selectedLanguage ===
                                "ALL" ||
                            snippet.language ===
                                selectedLanguage;

                        if (
                            !matchesVisibility ||
                            !matchesLanguage
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
                                String(
                                    value || ""
                                )
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

                        case "MOST_BOOKMARKED":
                            return (
                                getNumberValue(
                                    secondSnippet
                                        .bookmarkCount
                                ) -
                                getNumberValue(
                                    firstSnippet
                                        .bookmarkCount
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
            selectedVisibility,
            selectedLanguage,
            sortOption,
        ]);

    const hasActiveFilters =
        Boolean(searchText.trim()) ||
        selectedVisibility !== "ALL" ||
        selectedLanguage !== "ALL" ||
        sortOption !== "NEWEST";

    const clearFilters = () => {
        setSearchText("");
        setSelectedVisibility("ALL");
        setSelectedLanguage("ALL");
        setSortOption("NEWEST");
    };

    const openDeleteDialog = (
        snippet
    ) => {

        if (!snippet?.snippetId) {
            return;
        }

        setDeleteCandidate(snippet);
        setErrorMessage("");
        setSuccessMessage("");
    };

    const closeDeleteDialog = () => {

        if (deletingSnippetId) {
            return;
        }

        setDeleteCandidate(null);
    };

    const confirmDeleteSnippet =
        async () => {

            const snippetId =
                deleteCandidate?.snippetId;

            if (
                !snippetId ||
                deletingSnippetId
            ) {
                return;
            }

            try {
                setDeletingSnippetId(
                    snippetId
                );

                setErrorMessage("");
                setSuccessMessage("");

                const response =
                    await deleteSnippet(
                        snippetId
                    );

                setSnippets(
                    (previousSnippets) =>
                        previousSnippets.filter(
                            (snippet) =>
                                snippet.snippetId !==
                                snippetId
                        )
                );

                setSuccessMessage(
                    response?.message ||
                    "Snippet deleted successfully."
                );

                setDeleteCandidate(null);

                window.setTimeout(() => {
                    setSuccessMessage("");
                }, 3000);

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to delete snippet."
                );

            } finally {
                setDeletingSnippetId(
                    null
                );
            }
        };

    const formatCount = (value) => {

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
    };

    return (
        <main className="mySnippetsPage">

            <section className="mySnippetsHero">

                <div className="mySnippetsHeroContent">

                    <p className="mySnippetsEyebrow">
                        YOUR CODE LIBRARY
                    </p>

                    <h1>
                        My snippets
                    </h1>

                    <p>
                        Manage your public,
                        premium and private code
                        snippets from one place.
                    </p>

                    <div className="mySnippetsHeroActions">

                        <Link
                            to="/snippets/create"
                            className="mySnippetsCreateButton"
                        >
                            <FaPlus />

                            Create snippet
                        </Link>

                        <Link
                            to="/snippets"
                            className="mySnippetsExploreButton"
                        >
                            <FaCode />

                            Explore snippets
                        </Link>

                    </div>

                </div>

                <div className="mySnippetsHeroCode">
                    <span>const</span>
                    <strong>
                        snippets
                    </strong>
                    <span>=</span>
                    <em>
                        myCode
                    </em>
                    <span>;</span>
                </div>

            </section>

            <section className="mySnippetsContent">

                {successMessage && (
                    <div
                        className="mySnippetsAlert mySnippetsSuccessAlert"
                        role="status"
                    >
                        {successMessage}
                    </div>
                )}

                {errorMessage && (
                    <div
                        className="mySnippetsAlert mySnippetsErrorAlert"
                        role="alert"
                    >
                        {errorMessage}
                    </div>
                )}

                {!isLoading && (
                    <div className="mySnippetsStatistics">

                        <article>
                            <span>Total</span>

                            <strong>
                                {
                                    snippetStatistics.total
                                }
                            </strong>

                            <small>
                                Created snippets
                            </small>
                        </article>

                        <article>
                            <span>Public</span>

                            <strong>
                                {
                                    snippetStatistics.public
                                }
                            </strong>

                            <small>
                                Community visible
                            </small>
                        </article>

                        <article>
                            <span>Premium</span>

                            <strong>
                                {
                                    snippetStatistics.premium
                                }
                            </strong>

                            <small>
                                Premium access
                            </small>
                        </article>

                        <article>
                            <span>Private</span>

                            <strong>
                                {
                                    snippetStatistics.private
                                }
                            </strong>

                            <small>
                                Only visible to you
                            </small>
                        </article>

                        <article>
                            <span>Engagement</span>

                            <strong>
                                {formatCount(
                                    snippetStatistics
                                        .views
                                )}
                            </strong>

                            <small>
                                Total views
                            </small>
                        </article>

                        <article>
                            <span>Appreciation</span>

                            <strong>
                                {formatCount(
                                    snippetStatistics
                                        .likes
                                )}
                            </strong>

                            <small>
                                Total likes
                            </small>
                        </article>

                    </div>
                )}

                <div className="mySnippetsToolbar">

                    <div className="mySnippetsSearch">

                        <FaSearch />

                        <input
                            type="text"
                            value={searchText}
                            onChange={(event) =>
                                setSearchText(
                                    event.target.value
                                )
                            }
                            placeholder="Search your snippets..."
                            aria-label="Search my snippets"
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
                        className="mySnippetsMobileFilterButton"
                        onClick={() =>
                            setShowMobileFilters(
                                (
                                    previousValue
                                ) =>
                                    !previousValue
                            )
                        }
                    >
                        <FaFilter />

                        Filters
                    </button>

                    <button
                        type="button"
                        className="mySnippetsRefreshButton"
                        onClick={
                            loadMySnippets
                        }
                        disabled={isLoading}
                    >
                        <FaRedoAlt />

                        Refresh
                    </button>

                </div>

                <div
                    className={`mySnippetsFilters ${
                        showMobileFilters
                            ? "mySnippetsFiltersVisible"
                            : ""
                    }`}
                >

                    <div className="mySnippetsFilterField">

                        <label htmlFor="mySnippetVisibility">
                            Visibility
                        </label>

                        <select
                            id="mySnippetVisibility"
                            value={
                                selectedVisibility
                            }
                            onChange={(event) =>
                                setSelectedVisibility(
                                    event.target
                                        .value
                                )
                            }
                        >
                            {VISIBILITY_OPTIONS.map(
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

                    <div className="mySnippetsFilterField">

                        <label htmlFor="mySnippetLanguage">
                            Language
                        </label>

                        <select
                            id="mySnippetLanguage"
                            value={
                                selectedLanguage
                            }
                            onChange={(event) =>
                                setSelectedLanguage(
                                    event.target
                                        .value
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

                    <div className="mySnippetsFilterField">

                        <label htmlFor="mySnippetSort">
                            Sort by
                        </label>

                        <select
                            id="mySnippetSort"
                            value={sortOption}
                            onChange={(event) =>
                                setSortOption(
                                    event.target
                                        .value
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
                            className="mySnippetsClearButton"
                            onClick={
                                clearFilters
                            }
                        >
                            <FaTimes />

                            Clear filters
                        </button>
                    )}

                </div>

                {!isLoading &&
                    !errorMessage && (
                    <div className="mySnippetsResultHeader">

                        <div>
                            <h2>
                                Your code collection
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
                            <span>
                                Filtered results
                            </span>
                        )}

                    </div>
                )}

                {isLoading && (
                    <div className="mySnippetsGrid">

                        {Array.from({
                            length: 6,
                        }).map((_, index) => (
                            <div
                                key={index}
                                className="mySnippetSkeleton"
                            >
                                <div className="mySnippetSkeletonImage" />

                                <div className="mySnippetSkeletonBody">
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
                    <div className="mySnippetsState">

                        <span className="mySnippetsStateIcon mySnippetsErrorIcon">
                            <FaExclamationTriangle />
                        </span>

                        <h2>
                            Unable to load your
                            snippets
                        </h2>

                        <p>
                            {errorMessage}
                        </p>

                        <button
                            type="button"
                            onClick={
                                loadMySnippets
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
                    <div className="mySnippetsGrid">

                        {filteredSnippets.map(
                            (snippet) => (
                                <SnippetCard
                                    key={
                                        snippet.snippetId
                                    }
                                    snippet={
                                        snippet
                                    }
                                    showOwnerActions={
                                        true
                                    }
                                    deleting={
                                        deletingSnippetId ===
                                        snippet.snippetId
                                    }
                                    onDelete={
                                        openDeleteDialog
                                    }
                                />
                            )
                        )}

                    </div>
                )}

                {!isLoading &&
                    !errorMessage &&
                    filteredSnippets.length ===
                        0 && (
                    <div className="mySnippetsState">

                        <span className="mySnippetsStateIcon">
                            <FaCode />
                        </span>

                        <h2>
                            {snippets.length === 0
                                ? "You have not created any snippets yet"
                                : "No matching snippets found"}
                        </h2>

                        <p>
                            {snippets.length === 0
                                ? "Create your first snippet and start building your personal code library."
                                : "Try changing or clearing the current search and filters."}
                        </p>

                        {snippets.length === 0 ? (
                            <Link to="/snippets/create">
                                <FaPlus />

                                Create first snippet
                            </Link>
                        ) : (
                            <button
                                type="button"
                                onClick={
                                    clearFilters
                                }
                            >
                                <FaTimes />

                                Clear filters
                            </button>
                        )}

                    </div>
                )}

            </section>

            {deleteCandidate && (
                <div
                    className="mySnippetsModalBackdrop"
                    role="presentation"
                    onMouseDown={
                        closeDeleteDialog
                    }
                >
                    <div
                        className="mySnippetsDeleteModal"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="deleteSnippetTitle"
                        onMouseDown={(event) =>
                            event.stopPropagation()
                        }
                    >

                        <span className="mySnippetsDeleteIcon">
                            <FaTrash />
                        </span>

                        <h2 id="deleteSnippetTitle">
                            Delete snippet?
                        </h2>

                        <p>
                            You are about to delete{" "}
                            <strong>
                                “
                                {
                                    deleteCandidate.title
                                }
                                ”
                            </strong>
                            . This snippet will no
                            longer appear in your
                            active snippets.
                        </p>

                        <div className="mySnippetsDeleteActions">

                            <button
                                type="button"
                                className="mySnippetsModalCancelButton"
                                onClick={
                                    closeDeleteDialog
                                }
                                disabled={
                                    Boolean(
                                        deletingSnippetId
                                    )
                                }
                            >
                                Cancel
                            </button>

                            <button
                                type="button"
                                className="mySnippetsModalDeleteButton"
                                onClick={
                                    confirmDeleteSnippet
                                }
                                disabled={
                                    Boolean(
                                        deletingSnippetId
                                    )
                                }
                            >
                                {deletingSnippetId ? (
                                    <>
                                        <span className="mySnippetsSpinner" />

                                        Deleting...
                                    </>
                                ) : (
                                    <>
                                        <FaTrash />

                                        Delete snippet
                                    </>
                                )}
                            </button>

                        </div>

                    </div>
                </div>
            )}

        </main>
    );
}

function getTimeValue(dateValue) {

    if (!dateValue) {
        return 0;
    }

    const parsedDate =
        new Date(dateValue);

    return Number.isNaN(
        parsedDate.getTime()
    )
        ? 0
        : parsedDate.getTime();
}

function getNumberValue(value) {
    return Number(value) || 0;
}

export default MySnippets;