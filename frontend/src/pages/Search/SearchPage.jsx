import {
    useCallback,
    useEffect,
    useMemo,
    useRef,
    useState,
} from "react";

import {
    Link,
    useNavigate,
    useSearchParams,
} from "react-router-dom";

import {
    FaCode,
    FaCrown,
    FaFire,
    FaRedoAlt,
    FaSearch,
    FaTimes,
    FaUser,
    FaUsers,
} from "react-icons/fa";

import SnippetCard from "../../components/snippets/SnippetCard/SnippetCard";

import {
    getPopularSearches,
    getSearchHistory,
    getSuggestions,
    searchSnippets,
} from "../../services/searchService";

import {
    getPublicProfile,
} from "../../services/userService";

import {
    bookmarkSnippet,
    getSnippetBookmarkStatus,
    getUserSnippets,
    removeSnippetBookmark,
} from "../../services/snippetService";

import "./SearchPage.css";

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

const SEARCH_TABS = {
    SNIPPETS: "SNIPPETS",
    USERS: "USERS",
};

const AUTOCOMPLETE_DELAY = 350;

/*
 * API response kabhi direct hota hai:
 *
 * {
 *   userId: "...",
 *   username: "sakshi"
 * }
 *
 * Aur kabhi wrapper:
 *
 * {
 *   data: {
 *     userId: "...",
 *     username: "sakshi"
 *   }
 * }
 */
const unwrapResponseData = (response) => {
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
};

const normalizeSnippet = (snippet) => {
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
};

const normalizeUser = (user) => {
    return {
        ...user,

        userId:
            user?.userId ||
            user?.id,

        fullName:
            user?.fullName ||
            user?.name ||
            "CodeCanvas User",

        username:
            user?.username ||
            "",

        profileImage:
            user?.profileImage ||
            user?.profileImageUrl ||
            user?.avatarUrl ||
            null,

        bio:
            user?.bio ||
            "",

        followers:
            Number(
                user?.followers ??
                user?.followerCount
            ) || 0,

        following:
            Number(
                user?.following ??
                user?.followingCount
            ) || 0,

        snippetCount:
            Number(
                user?.snippetCount ??
                user?.totalSnippets
            ) || 0,

        premium:
            Boolean(
                user?.premium ??
                user?.isPremium ??
                user?.role === "PREMIUM"
            ),

        tier:
            user?.tier ||
            user?.subscriptionTier ||
            user?.role ||
            "",
    };
};

const getSuggestionText = (suggestion) => {
    if (typeof suggestion === "string") {
        return suggestion;
    }

    return (
        suggestion?.keyword ||
        suggestion?.suggestion ||
        suggestion?.title ||
        suggestion?.value ||
        ""
    );
};

const buildPublicProfilePath = (username) => {
    return `/users/${encodeURIComponent(
        username
    )}`;
};

/*
 * Tumhara backend missing user ke liye actual HTTP 404 ke
 * badle kabhi 400 ke andar "404 NOT_FOUND User not found"
 * bhej raha hai.
 *
 * Dono situations ko normal empty result treat karenge.
 */
const isUserNotFoundError = (error) => {
    const status =
        error?.response?.status;

    const responseData =
        error?.response?.data;

    const message = String(
        responseData?.message ||
        responseData?.error ||
        responseData ||
        error?.message ||
        ""
    ).toLowerCase();

    return (
        status === 404 ||
        (
            status === 400 &&
            (
                message.includes("user not found") ||
                message.includes("not_found") ||
                message.includes("not found")
            )
        )
    );
};

export default function SearchPage() {
    const navigate = useNavigate();

    const [
        searchParams,
        setSearchParams,
    ] = useSearchParams();

    const autocompleteRequestId =
        useRef(0);

    const token =
        localStorage.getItem("token");

    const queryFromUrl =
        searchParams.get("q") || "";

    const tabFromUrl =
        searchParams.get("type") === "users"
            ? SEARCH_TABS.USERS
            : SEARCH_TABS.SNIPPETS;

    const [activeTab, setActiveTab] =
        useState(tabFromUrl);

    const [searchText, setSearchText] =
        useState(queryFromUrl);

    const [
        selectedCategory,
        setSelectedCategory,
    ] = useState("All");

    const [snippets, setSnippets] =
        useState([]);

    const [users, setUsers] =
        useState([]);

    const [
        matchedUser,
        setMatchedUser,
    ] = useState(null);

    const [
        snippetSuggestions,
        setSnippetSuggestions,
    ] = useState([]);

    const [
        popularSearches,
        setPopularSearches,
    ] = useState([]);

    const [
        searchHistory,
        setSearchHistory,
    ] = useState([]);

    const [snippetTotal, setSnippetTotal] =
        useState(0);

    const [userTotal, setUserTotal] =
        useState(0);

    const [currentPage, setCurrentPage] =
        useState(0);

    const [loading, setLoading] =
        useState(true);

    const [
        suggestionLoading,
        setSuggestionLoading,
    ] = useState(false);

    const [
        errorMessage,
        setErrorMessage,
    ] = useState("");

    const [
        actionMessage,
        setActionMessage,
    ] = useState("");

    const [
        showSuggestionPanel,
        setShowSuggestionPanel,
    ] = useState(false);

    const [
        bookmarkStatus,
        setBookmarkStatus,
    ] = useState({});

    const [
        bookmarkLoading,
        setBookmarkLoading,
    ] = useState({});

    /*
     * Browser URL se state sync.
     */
    useEffect(() => {
        setSearchText(queryFromUrl);
        setActiveTab(tabFromUrl);
    }, [
        queryFromUrl,
        tabFromUrl,
    ]);

    /*
     * Snippet cards ke bookmark statuses.
     */
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

                const entries =
                    await Promise.all(
                        snippetList.map(
                            async (snippet) => {
                                try {
                                    const response =
                                        await getSnippetBookmarkStatus(
                                            snippet.snippetId
                                        );

                                    const payload =
                                        unwrapResponseData(
                                            response
                                        );

                                    return [
                                        snippet.snippetId,
                                        Boolean(
                                            payload?.bookmarked
                                        ),
                                    ];
                                } catch (bookmarkError) {
                                    console.error(
                                        "Bookmark status error:",
                                        bookmarkError
                                    );

                                    return [
                                        snippet.snippetId,
                                        false,
                                    ];
                                }
                            }
                        )
                    );

                setBookmarkStatus(
                    Object.fromEntries(entries)
                );
            },
            [token]
        );

    /*
     * Normal snippet keyword search.
     *
     * Ye tab chalega jab entered keyword se exact username
     * ka user nahi milta.
     */
    const fetchNormalSnippetSearch =
        useCallback(
            async (keyword) => {
                const response =
                    await searchSnippets({
                        keyword:
                            keyword || null,

                        language:
                            selectedCategory === "All"
                                ? null
                                : selectedCategory,

                        framework: null,
                        category: null,
                        sortBy: null,
                        page: currentPage,
                        size: 12,
                    });

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
                    rawSnippets.map(
                        normalizeSnippet
                    );

                setSnippets(
                    normalizedSnippets
                );

                setSnippetTotal(
                    Number(
                        payload?.totalElements ??
                        payload?.totalResults ??
                        payload?.total
                    ) ||
                    normalizedSnippets.length
                );

                await loadBookmarkStatuses(
                    normalizedSnippets
                );
            },
            [
                selectedCategory,
                currentPage,
                loadBookmarkStatuses,
            ]
        );

    /*
     * Main combined search flow.
     *
     * Search "sakshi"
     *      ↓
     * GET /api/users/public/sakshi
     *      ↓
     * User found
     *      ↓
     * GET snippets using profile.userId
     *      ↓
     * Users tab = user card
     * Snippets tab = that user's snippets
     *
     * Exact user nahi mila:
     *      ↓
     * Normal snippet keyword search
     */
    const fetchSearchResults =
        useCallback(async () => {
            const normalizedQuery =
                queryFromUrl.trim();

            try {
                setLoading(true);
                setErrorMessage("");

                setUsers([]);
                setUserTotal(0);
                setMatchedUser(null);

                /*
                 * Empty query par normal popular/all
                 * snippet search chalegi.
                 */
                if (!normalizedQuery) {
                    await fetchNormalSnippetSearch("");
                    return;
                }

                try {
                    const profileResponse =
                        await getPublicProfile(
                            normalizedQuery
                        );

                    const profilePayload =
                        unwrapResponseData(
                            profileResponse
                        );

                    const rawProfile =
                        profilePayload?.user ||
                        profilePayload?.profile ||
                        profilePayload;

                    if (
                        rawProfile &&
                        rawProfile.username &&
                        rawProfile.userId
                    ) {
                        const normalizedUser =
                            normalizeUser(
                                rawProfile
                            );

                        setMatchedUser(
                            normalizedUser
                        );

                        setUsers([
                            normalizedUser,
                        ]);

                        setUserTotal(1);

                        /*
                         * Exact user mil gaya.
                         * Ab us user ke uploaded snippets fetch honge.
                         */
                        const snippetResponse =
                            await getUserSnippets(
                                normalizedUser.userId
                            );

                        const snippetPayload =
                            unwrapResponseData(
                                snippetResponse
                            );

                        const rawUserSnippets =
                            Array.isArray(
                                snippetPayload
                            )
                                ? snippetPayload
                                : Array.isArray(
                                    snippetPayload?.snippets
                                )
                                    ? snippetPayload.snippets
                                    : Array.isArray(
                                        snippetPayload?.content
                                    )
                                        ? snippetPayload.content
                                        : [];

                        const normalizedUserSnippets =
                            rawUserSnippets.map(
                                normalizeSnippet
                            );

                        setSnippets(
                            normalizedUserSnippets
                        );

                        setSnippetTotal(
                            normalizedUserSnippets.length
                        );

                        await loadBookmarkStatuses(
                            normalizedUserSnippets
                        );

                        return;
                    }

                    /*
                     * Response successful tha lekin valid profile
                     * nahi mila. Normal snippet search fallback.
                     */
                    await fetchNormalSnippetSearch(
                        normalizedQuery
                    );
                } catch (profileError) {
                    /*
                     * Exact username nahi mila.
                     *
                     * Isko page error nahi banayenge.
                     * Keyword ko normal snippet search mein use karenge.
                     */
                    if (
                        isUserNotFoundError(
                            profileError
                        )
                    ) {
                        setUsers([]);
                        setUserTotal(0);
                        setMatchedUser(null);

                        await fetchNormalSnippetSearch(
                            normalizedQuery
                        );

                        return;
                    }

                    throw profileError;
                }
            } catch (requestError) {
                console.error(
                    "Search page loading error:",
                    requestError
                );

                setSnippets([]);
                setSnippetTotal(0);
                setUsers([]);
                setUserTotal(0);
                setMatchedUser(null);

                setErrorMessage(
                    requestError
                        ?.response?.data
                        ?.message ||
                    requestError?.message ||
                    "Unable to load search results."
                );
            } finally {
                setLoading(false);
            }
        }, [
            queryFromUrl,
            fetchNormalSnippetSearch,
            loadBookmarkStatuses,
        ]);

    /*
     * Query, category ya page change par combined search.
     */
    useEffect(() => {
        fetchSearchResults();
    }, [fetchSearchResults]);

    /*
     * Popular searches.
     */
    useEffect(() => {
        const loadPopularSearches =
            async () => {
                try {
                    const response =
                        await getPopularSearches();

                    const payload =
                        unwrapResponseData(
                            response
                        );

                    const values =
                        Array.isArray(payload)
                            ? payload
                            : Array.isArray(
                                payload?.searches
                            )
                                ? payload.searches
                                : [];

                    setPopularSearches(
                        values
                    );
                } catch (requestError) {
                    console.error(
                        "Popular searches error:",
                        requestError
                    );

                    setPopularSearches([]);
                }
            };

        loadPopularSearches();
    }, []);

    /*
     * Logged-in user search history.
     */
    useEffect(() => {
        if (!token) {
            setSearchHistory([]);
            return;
        }

        const loadSearchHistory =
            async () => {
                try {
                    const response =
                        await getSearchHistory();

                    const payload =
                        unwrapResponseData(
                            response
                        );

                    const values =
                        Array.isArray(payload)
                            ? payload
                            : Array.isArray(
                                payload?.history
                            )
                                ? payload.history
                                : [];

                    setSearchHistory(
                        values
                    );
                } catch (requestError) {
                    console.error(
                        "Search history error:",
                        requestError
                    );

                    setSearchHistory([]);
                }
            };

        loadSearchHistory();
    }, [token]);

    /*
     * Snippet autocomplete only.
     *
     * User search ab exact username User Service se hoti hai,
     * isliye /api/search/users/suggestions use nahi hoga.
     */
    useEffect(() => {
        const normalizedKeyword =
            searchText.trim();

        autocompleteRequestId.current += 1;

        const currentRequestId =
            autocompleteRequestId.current;

        if (
            activeTab === SEARCH_TABS.USERS ||
            normalizedKeyword.length < 2
        ) {
            setSnippetSuggestions([]);
            setSuggestionLoading(false);
            return;
        }

        const timeoutId =
            window.setTimeout(
                async () => {
                    try {
                        setSuggestionLoading(
                            true
                        );

                        const response =
                            await getSuggestions(
                                normalizedKeyword
                            );

                        if (
                            currentRequestId !==
                            autocompleteRequestId
                                .current
                        ) {
                            return;
                        }

                        const payload =
                            unwrapResponseData(
                                response
                            );

                        const rawSuggestions =
                            Array.isArray(payload)
                                ? payload
                                : Array.isArray(
                                    payload?.suggestions
                                )
                                    ? payload.suggestions
                                    : [];

                        setSnippetSuggestions(
                            rawSuggestions
                        );
                    } catch (requestError) {
                        console.error(
                            "Snippet autocomplete error:",
                            requestError
                        );

                        if (
                            currentRequestId ===
                            autocompleteRequestId
                                .current
                        ) {
                            setSnippetSuggestions(
                                []
                            );
                        }
                    } finally {
                        if (
                            currentRequestId ===
                            autocompleteRequestId
                                .current
                        ) {
                            setSuggestionLoading(
                                false
                            );
                        }
                    }
                },
                AUTOCOMPLETE_DELAY
            );

        return () =>
            window.clearTimeout(
                timeoutId
            );
    }, [
        searchText,
        activeTab,
    ]);

    const updateUrl = (
        keyword,
        tab
    ) => {
        const updatedParams =
            new URLSearchParams();

        if (keyword?.trim()) {
            updatedParams.set(
                "q",
                keyword.trim()
            );
        }

        if (
            tab === SEARCH_TABS.USERS
        ) {
            updatedParams.set(
                "type",
                "users"
            );
        }

        setSearchParams(
            updatedParams,
            {
                replace: false,
            }
        );
    };

    const executeSearch = (value) => {
        const normalizedValue =
            String(value || "").trim();

        setCurrentPage(0);
        setSelectedCategory("All");
        setShowSuggestionPanel(false);
        setSnippetSuggestions([]);

        updateUrl(
            normalizedValue,
            activeTab
        );
    };

    const handleSearchSubmit = (event) => {
        event.preventDefault();

        executeSearch(
            searchText
        );
    };

    const handleQuickSearch = (value) => {
        const normalizedValue =
            String(value || "").trim();

        if (!normalizedValue) {
            return;
        }

        setSearchText(
            normalizedValue
        );

        executeSearch(
            normalizedValue
        );
    };

    const clearSearch = () => {
        setSearchText("");
        setCurrentPage(0);
        setSelectedCategory("All");
        setShowSuggestionPanel(false);
        setSnippetSuggestions([]);

        setUsers([]);
        setUserTotal(0);
        setMatchedUser(null);

        updateUrl(
            "",
            activeTab
        );
    };

    const handleTabChange = (nextTab) => {
        setActiveTab(nextTab);
        setErrorMessage("");
        setShowSuggestionPanel(false);
        setSnippetSuggestions([]);

        /*
         * Same searched username/query preserve rahega.
         *
         * Snippets tab:
         * /search?q=sakshi
         *
         * Users tab:
         * /search?q=sakshi&type=users
         */
        updateUrl(
            queryFromUrl,
            nextTab
        );
    };

    const handleCategoryChange = (category) => {
        setSelectedCategory(
            category
        );

        setCurrentPage(0);
    };

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
                                `/search${window.location.search}`,
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

                        [snippetId]:
                            true,
                    })
                );

                setErrorMessage("");
                setActionMessage("");

                const response =
                    currentlyBookmarked
                        ? await removeSnippetBookmark(
                            snippetId
                        )
                        : await bookmarkSnippet(
                            snippetId
                        );

                const payload =
                    unwrapResponseData(
                        response
                    );

                const nextBookmarked =
                    Boolean(
                        payload?.bookmarked
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
                                    existingSnippet
                                        .snippetId !==
                                    snippetId
                                ) {
                                    return existingSnippet;
                                }

                                return {
                                    ...existingSnippet,

                                    bookmarkCount:
                                        Number(
                                            payload
                                                ?.bookmarkCount
                                        ) || 0,
                                };
                            }
                        )
                );

                setActionMessage(
                    payload?.message ||
                    (
                        nextBookmarked
                            ? "Snippet bookmarked successfully."
                            : "Bookmark removed successfully."
                    )
                );

                window.setTimeout(
                    () => {
                        setActionMessage("");
                    },
                    2500
                );
            } catch (requestError) {
                setErrorMessage(
                    requestError
                        ?.response?.data
                        ?.message ||
                    requestError?.message ||
                    "Unable to update bookmark."
                );
            } finally {
                setBookmarkLoading(
                    (previousState) => ({
                        ...previousState,

                        [snippetId]:
                            false,
                    })
                );
            }
        };

    const historyItems =
        useMemo(() => {
            return searchHistory
                .map((historyItem) => {
                    if (
                        typeof historyItem ===
                        "string"
                    ) {
                        return historyItem;
                    }

                    return (
                        historyItem?.keyword ||
                        historyItem?.searchTerm ||
                        historyItem?.query ||
                        ""
                    );
                })
                .filter(Boolean)
                .slice(0, 5);
        }, [searchHistory]);

    const popularItems =
        useMemo(() => {
            return popularSearches
                .map((searchItem) => {
                    if (
                        typeof searchItem ===
                        "string"
                    ) {
                        return searchItem;
                    }

                    return (
                        searchItem?.keyword ||
                        searchItem?.searchTerm ||
                        searchItem?.query ||
                        ""
                    );
                })
                .filter(Boolean)
                .slice(0, 5);
        }, [popularSearches]);

    /*
     * Exact user milne par category filtering client side hogi,
     * kyunki snippets already getUserSnippets(userId) se aa chuki hain.
     */
    const visibleSnippets =
        useMemo(() => {
            if (
                !matchedUser ||
                selectedCategory === "All"
            ) {
                return snippets;
            }

            const selectedValue =
                selectedCategory
                    .trim()
                    .toLowerCase();

            return snippets.filter(
                (snippet) => {
                    const language =
                        String(
                            snippet?.language || ""
                        )
                            .trim()
                            .toLowerCase();

                    const framework =
                        String(
                            snippet?.framework || ""
                        )
                            .trim()
                            .toLowerCase();

                    return (
                        language === selectedValue ||
                        framework === selectedValue
                    );
                }
            );
        }, [
            snippets,
            selectedCategory,
            matchedUser,
        ]);

    const showRecentHistory =
        showSuggestionPanel &&
        activeTab ===
            SEARCH_TABS.SNIPPETS &&
        searchText.trim().length === 0 &&
        historyItems.length > 0;

    const showSnippetSuggestions =
        showSuggestionPanel &&
        activeTab ===
            SEARCH_TABS.SNIPPETS &&
        searchText.trim().length >= 2 &&
        snippetSuggestions.length > 0;

    const showSuggestionLoader =
        showSuggestionPanel &&
        activeTab ===
            SEARCH_TABS.SNIPPETS &&
        searchText.trim().length >= 2 &&
        suggestionLoading;

    const currentTotal =
        activeTab === SEARCH_TABS.SNIPPETS
            ? (
                matchedUser
                    ? visibleSnippets.length
                    : snippetTotal
            )
            : userTotal;

    const currentResultCount =
        activeTab === SEARCH_TABS.SNIPPETS
            ? visibleSnippets.length
            : users.length;

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
                        Search an exact username to view
                        that developer and their uploaded
                        snippets, or search reusable code
                        by keyword.
                    </p>

                    <div className="searchFormContainer">

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
                                placeholder={
                                    activeTab ===
                                    SEARCH_TABS.USERS
                                        ? "Enter exact username..."
                                        : "Search username, Java, React, Kafka..."
                                }
                                autoComplete="off"
                                aria-label="Search CodeCanvas"
                                onFocus={() =>
                                    setShowSuggestionPanel(
                                        true
                                    )
                                }
                                onBlur={() => {
                                    window.setTimeout(
                                        () =>
                                            setShowSuggestionPanel(
                                                false
                                            ),
                                        180
                                    );
                                }}
                                onChange={(event) => {
                                    setSearchText(
                                        event.target.value
                                    );

                                    setShowSuggestionPanel(
                                        true
                                    );
                                }}
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

                        {(showRecentHistory ||
                            showSnippetSuggestions ||
                            showSuggestionLoader) && (

                            <div className="searchSuggestionPanel">

                                {showSuggestionLoader && (
                                    <div className="searchSuggestionLoading">
                                        <span />
                                        Loading suggestions...
                                    </div>
                                )}

                                {!suggestionLoading &&
                                    showRecentHistory && (

                                    <div className="searchSuggestionGroup">

                                        <p>
                                            Recent searches
                                        </p>

                                        {historyItems.map(
                                            (historyItem) => (

                                                <button
                                                    key={
                                                        historyItem
                                                    }
                                                    type="button"
                                                    onMouseDown={(
                                                        event
                                                    ) =>
                                                        event
                                                            .preventDefault()
                                                    }
                                                    onClick={() =>
                                                        handleQuickSearch(
                                                            historyItem
                                                        )
                                                    }
                                                >
                                                    <FaSearch />

                                                    <span>
                                                        {historyItem}
                                                    </span>
                                                </button>

                                            )
                                        )}

                                    </div>
                                )}

                                {!suggestionLoading &&
                                    showSnippetSuggestions && (

                                    <div className="searchSuggestionGroup">

                                        <p>
                                            Snippet suggestions
                                        </p>

                                        {snippetSuggestions.map(
                                            (
                                                suggestion,
                                                index
                                            ) => {
                                                const text =
                                                    getSuggestionText(
                                                        suggestion
                                                    );

                                                if (!text) {
                                                    return null;
                                                }

                                                return (
                                                    <button
                                                        key={
                                                            suggestion
                                                                ?.snippetId ||
                                                            `${text}-${index}`
                                                        }
                                                        type="button"
                                                        onMouseDown={(
                                                            event
                                                        ) =>
                                                            event
                                                                .preventDefault()
                                                        }
                                                        onClick={() =>
                                                            handleQuickSearch(
                                                                text
                                                            )
                                                        }
                                                    >
                                                        <FaCode />

                                                        <span>
                                                            {text}
                                                        </span>
                                                    </button>
                                                );
                                            }
                                        )}

                                    </div>
                                )}

                            </div>
                        )}

                    </div>

                    {popularItems.length > 0 && (

                        <div className="trendingSearches">

                            <span>
                                Trending:
                            </span>

                            {popularItems.map(
                                (popularItem) => (

                                    <button
                                        key={popularItem}
                                        type="button"
                                        onClick={() =>
                                            handleQuickSearch(
                                                popularItem
                                            )
                                        }
                                    >
                                        {popularItem}
                                    </button>

                                )
                            )}

                        </div>
                    )}

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
                                developer ={" "}
                                <strong>
                                    search
                                </strong>
                                (username);
                            </p>

                            <p>
                                <span>const</span>{" "}
                                snippets ={" "}
                                <strong>
                                    developer
                                </strong>
                                .snippets;
                            </p>

                            <p>
                                <em>return</em>{" "}
                                results;
                            </p>

                        </div>

                    </div>

                </div>

            </section>

            {/* ================= CONTENT ================= */}

            <section className="searchPageContent">

                {actionMessage && (
                    <div
                        className="searchActionMessage"
                        role="status"
                    >
                        {actionMessage}
                    </div>
                )}

                <div className="searchTypeTabs">

                    <button
                        type="button"
                        className={
                            activeTab ===
                            SEARCH_TABS.SNIPPETS
                                ? "activeSearchTypeTab"
                                : ""
                        }
                        onClick={() =>
                            handleTabChange(
                                SEARCH_TABS.SNIPPETS
                            )
                        }
                    >
                        <FaCode />

                        Snippets

                        <span>
                            {
                                matchedUser
                                    ? snippets.length
                                    : snippetTotal
                            }
                        </span>
                    </button>

                    <button
                        type="button"
                        className={
                            activeTab ===
                            SEARCH_TABS.USERS
                                ? "activeSearchTypeTab"
                                : ""
                        }
                        onClick={() =>
                            handleTabChange(
                                SEARCH_TABS.USERS
                            )
                        }
                    >
                        <FaUsers />

                        Users

                        <span>
                            {userTotal}
                        </span>
                    </button>

                </div>

                {activeTab ===
                    SEARCH_TABS.SNIPPETS && (

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
                                        handleCategoryChange(
                                            category
                                        )
                                    }
                                >
                                    {category}
                                </button>

                            )
                        )}

                    </div>
                )}

                <div className="searchResultHeading">

                    <div>

                        <p>
                            {queryFromUrl
                                ? "SEARCH RESULTS"
                                : "EXPLORE"}
                        </p>

                        <h2>
                            {activeTab ===
                            SEARCH_TABS.USERS
                                ? matchedUser
                                    ? `User matching “${queryFromUrl}”`
                                    : queryFromUrl
                                        ? `Users matching “${queryFromUrl}”`
                                        : "Search developers"
                                : matchedUser
                                    ? `${matchedUser.fullName}'s snippets`
                                    : queryFromUrl
                                        ? `Results for “${queryFromUrl}”`
                                        : "Popular snippets"}
                        </h2>

                    </div>

                    <span>
                        {currentTotal}{" "}
                        {currentTotal === 1
                            ? "result"
                            : "results"}
                    </span>

                </div>

                {/* ================= ERROR ================= */}

                {errorMessage && (

                    <div className="searchErrorState">

                        <span>!</span>

                        <h2>
                            Unable to load results
                        </h2>

                        <p>
                            {errorMessage}
                        </p>

                        <button
                            type="button"
                            onClick={
                                fetchSearchResults
                            }
                        >
                            <FaRedoAlt />
                            Try again
                        </button>

                    </div>
                )}

                {/* ================= SNIPPET LOADING ================= */}

                {loading &&
                    !errorMessage &&
                    activeTab ===
                        SEARCH_TABS.SNIPPETS && (

                    <div className="searchSnippetGrid">

                        {Array.from({
                            length: 8,
                        }).map(
                            (_, index) => (

                                <div
                                    key={index}
                                    className="searchSnippetSkeleton"
                                >
                                    <div className="searchSnippetSkeletonHeader" />

                                    <div className="searchSnippetSkeletonImage" />

                                    <div className="searchSnippetSkeletonBody">
                                        <span />
                                        <span />
                                        <span />
                                        <span />
                                    </div>
                                </div>

                            )
                        )}

                    </div>
                )}

                {/* ================= USER LOADING ================= */}

                {loading &&
                    !errorMessage &&
                    activeTab ===
                        SEARCH_TABS.USERS && (

                    <div className="searchUserGrid">

                        <div className="searchUserSkeleton">

                            <span />

                            <div>
                                <strong />
                                <small />
                                <small />
                            </div>

                        </div>

                    </div>
                )}

                {/* ================= SNIPPET RESULTS ================= */}

                {!loading &&
                    !errorMessage &&
                    activeTab ===
                        SEARCH_TABS.SNIPPETS &&
                    visibleSnippets.length > 0 && (

                    <div className="searchSnippetGrid">

                        {visibleSnippets.map(
                            (snippet) => (

                                <div
                                    key={
                                        snippet.snippetId
                                    }
                                    className="searchSnippetCardWrapper"
                                >

                                    <SnippetCard
                                        snippet={snippet}
                                        showOwnerActions={false}
                                        showBookmarkAction={true}
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
                                        snippet.snippetId
                                    ] && (

                                        <div className="searchBookmarkLoader">
                                            <span />
                                        </div>
                                    )}

                                </div>

                            )
                        )}

                    </div>
                )}

                {/* ================= USER RESULT ================= */}

                {!loading &&
                    !errorMessage &&
                    activeTab ===
                        SEARCH_TABS.USERS &&
                    users.length > 0 && (

                    <div className="searchUserGrid">

                        {users.map(
                            (user) => (

                                <Link
                                    key={
                                        user.userId ||
                                        user.username
                                    }
                                    to={
                                        buildPublicProfilePath(
                                            user.username
                                        )
                                    }
                                    className="searchUserResultCard"
                                >

                                    <div className="searchUserResultHeader">

                                        <UserAvatar
                                            user={user}
                                        />

                                        <div className="searchUserIdentity">

                                            <h3>
                                                {user.fullName}
                                            </h3>

                                            <p>
                                                @{user.username}
                                            </p>

                                        </div>

                                        {user.premium && (

                                            <span className="searchPremiumUserBadge">

                                                <FaCrown />

                                                Premium

                                            </span>
                                        )}

                                    </div>

                                    <p className="searchUserBio">

                                        {user.bio?.trim()
                                            ? user.bio
                                            : "CodeCanvas developer and community member."}

                                    </p>

                                    <div className="searchUserStatistics">

                                        <span>
                                            <strong>
                                                {user.followers}
                                            </strong>

                                            Followers
                                        </span>

                                        <span>
                                            <strong>
                                                {user.following}
                                            </strong>

                                            Following
                                        </span>

                                        <span>
                                            <strong>
                                                {
                                                    snippets.length
                                                }
                                            </strong>

                                            Snippets
                                        </span>

                                    </div>

                                    <span className="searchViewProfileButton">
                                        View profile
                                    </span>

                                </Link>

                            )
                        )}

                    </div>
                )}

                {/* ================= EMPTY STATE ================= */}

                {!loading &&
                    !errorMessage &&
                    currentResultCount === 0 && (

                    <div className="searchEmptyState">

                        <span>
                            {activeTab ===
                            SEARCH_TABS.USERS
                                ? <FaUser />
                                : <FaSearch />}
                        </span>

                        <h2>
                            {activeTab ===
                            SEARCH_TABS.USERS
                                ? queryFromUrl
                                    ? "User not found"
                                    : "Search for a user"
                                : matchedUser
                                    ? "No snippets uploaded"
                                    : "No snippets found"}
                        </h2>

                        <p>
                            {activeTab ===
                            SEARCH_TABS.USERS
                                ? queryFromUrl
                                    ? "Enter the exact username used by the developer."
                                    : "Enter an exact username, for example: sakshi"
                                : matchedUser
                                    ? `${matchedUser.fullName} has not uploaded any active snippets yet.`
                                    : "Try another keyword or select a different category."}
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
                            {activeTab ===
                            SEARCH_TABS.USERS
                                ? <FaUsers />
                                : <FaCode />}

                            {activeTab ===
                            SEARCH_TABS.USERS
                                ? "Clear username"
                                : "Explore all"}
                        </button>

                    </div>
                )}

            </section>

        </main>
    );
}

function UserAvatar({ user }) {
    return (
        <div className="searchUserAvatar">

            {user?.profileImage ? (

                <img
                    src={user.profileImage}
                    alt={
                        user.fullName ||
                        user.username ||
                        "User"
                    }
                />

            ) : (

                <FaUser />

            )}

        </div>
    );
}