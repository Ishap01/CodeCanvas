import React, {
    useCallback,
    useEffect,
    useMemo,
    useState,
} from "react";

import {
    Link,
    useNavigate,
    useParams,
} from "react-router-dom";

import {
    FaArrowLeft,
    FaBookmark,
    FaCode,
    FaComment,
    FaCopy,
    FaEdit,
    FaEye,
    FaHeart,
    FaLock,
    FaReply,
    FaShareAlt,
    FaTrash,
    FaUser,
} from "react-icons/fa";

import {
    addSnippetComment,
    bookmarkSnippet,
    deleteSnippetComment,
    forkSnippet,
    getCommentReplies,
    getSnippetBookmarkStatus,
    getSnippetById,
    getSnippetComments,
    getSnippetLikeStatus,
    likeSnippet,
    removeSnippetBookmark,
    replyToSnippetComment,
    unlikeSnippet,
    updateSnippetComment,
} from "../../../services/snippetService";

import {
    getUserById,
} from "../../../services/userService";

import "./SnippetDetails.css";

function SnippetDetails() {

    const { snippetId } = useParams();

    const navigate = useNavigate();

    const token =
        localStorage.getItem("token");

    const storedUser = useMemo(() => {

        try {
            const userValue =
                localStorage.getItem("user");

            return userValue
                ? JSON.parse(userValue)
                : null;

        } catch {
            return null;
        }

    }, []);

    const currentUserId =
        storedUser?.userId ||
        storedUser?.id ||
        null;

    const [snippet, setSnippet] =
        useState(null);

    /*
     * Snippet owner ka complete user profile.
     *
     * Isme:
     * - fullName
     * - username
     * - profileImage
     * - userId
     *
     * store hoga.
     */
    const [ownerProfile, setOwnerProfile] =
        useState(null);

    const [ownerLoading, setOwnerLoading] =
        useState(false);

    const [comments, setComments] =
        useState([]);

    const [replyMap, setReplyMap] =
        useState({});

    const [expandedReplies, setExpandedReplies] =
        useState({});

    const [commentText, setCommentText] =
        useState("");

    const [replyText, setReplyText] =
        useState({});

    const [editingCommentId, setEditingCommentId] =
        useState(null);

    const [editingContent, setEditingContent] =
        useState("");

    const [liked, setLiked] =
        useState(false);

    const [bookmarked, setBookmarked] =
        useState(false);

    const [isLoading, setIsLoading] =
        useState(true);

    const [commentsLoading, setCommentsLoading] =
        useState(false);

    const [actionLoading, setActionLoading] =
        useState("");

    const [errorMessage, setErrorMessage] =
        useState("");

    const [successMessage, setSuccessMessage] =
        useState("");

    const [copySuccess, setCopySuccess] =
        useState(false);

    const isOwner =
        Boolean(
            currentUserId &&
            snippet?.userId &&
            String(currentUserId) ===
            String(snippet.userId)
        );

    /*
     * API kabhi direct data return karegi:
     *
     * {
     *     userId,
     *     username
     * }
     *
     * Ya wrapper:
     *
     * {
     *     data: {
     *         userId,
     *         username
     *     }
     * }
     */
    const unwrapResponseData = (
        response
    ) => {

        if (!response) {
            return null;
        }

        if (
            typeof response === "object" &&
            response.data !== undefined
        ) {
            return response.data;
        }

        return response;
    };

    /*
     * Snippet load.
     */
    const loadSnippet =
        useCallback(async () => {

            if (!snippetId) {
                return;
            }

            try {
                setIsLoading(true);
                setErrorMessage("");

                const response =
                    await getSnippetById(
                        snippetId
                    );

                const snippetData =
                    unwrapResponseData(
                        response
                    );

                setSnippet(
                    snippetData
                );

            } catch (error) {

                setSnippet(null);

                setErrorMessage(
                    error.message ||
                    "Unable to load snippet."
                );

            } finally {
                setIsLoading(false);
            }

        }, [snippetId]);

    /*
     * Snippet userId se owner profile load karega.
     *
     * Flow:
     *
     * snippet.userId
     *      ↓
     * GET /api/users/{userId}
     *      ↓
     * owner fullName, username, profileImage
     */
    const loadOwnerProfile =
        useCallback(async () => {

            if (!snippet?.userId) {
                setOwnerProfile(null);
                return;
            }

            try {
                setOwnerLoading(true);

                const response =
                    await getUserById(
                        snippet.userId
                    );

                const payload =
                    unwrapResponseData(
                        response
                    );

                const userData =
                    payload?.user ||
                    payload?.profile ||
                    payload;

                if (
                    userData &&
                    (
                        userData.userId ||
                        userData.id ||
                        userData.username
                    )
                ) {
                    setOwnerProfile({
                        ...userData,

                        userId:
                            userData.userId ||
                            userData.id ||
                            snippet.userId,

                        fullName:
                            userData.fullName ||
                            userData.name ||
                            "CodeCanvas User",

                        username:
                            userData.username ||
                            "",

                        profileImage:
                            userData.profileImage ||
                            userData.profileImageUrl ||
                            userData.avatarUrl ||
                            null,
                    });

                    return;
                }

                setOwnerProfile(null);

            } catch (error) {

                /*
                 * Owner profile fail hone par snippet page
                 * crash nahi hona chahiye.
                 *
                 * Raw user ID fallback mein dikhegi.
                 */
                console.error(
                    "Unable to load snippet owner:",
                    error
                );

                setOwnerProfile(null);

            } finally {
                setOwnerLoading(false);
            }

        }, [
            snippet?.userId,
        ]);

    const loadComments =
        useCallback(async () => {

            if (!snippetId) {
                return;
            }

            try {
                setCommentsLoading(true);

                const response =
                    await getSnippetComments(
                        snippetId
                    );

                setComments(
                    Array.isArray(response)
                        ? response
                        : []
                );

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to load comments."
                );

            } finally {
                setCommentsLoading(false);
            }

        }, [snippetId]);

    const loadEngagementStatus =
        useCallback(async () => {

            if (!token || !snippetId) {
                return;
            }

            const results =
                await Promise.allSettled([
                    getSnippetLikeStatus(
                        snippetId
                    ),
                    getSnippetBookmarkStatus(
                        snippetId
                    ),
                ]);

            const likeResult =
                results[0];

            const bookmarkResult =
                results[1];

            if (
                likeResult.status ===
                "fulfilled"
            ) {
                setLiked(
                    Boolean(
                        likeResult.value?.liked
                    )
                );
            }

            if (
                bookmarkResult.status ===
                "fulfilled"
            ) {
                setBookmarked(
                    Boolean(
                        bookmarkResult
                            .value
                            ?.bookmarked
                    )
                );
            }

        }, [snippetId, token]);

    useEffect(() => {
        loadSnippet();
        loadComments();
        loadEngagementStatus();
    }, [
        loadSnippet,
        loadComments,
        loadEngagementStatus,
    ]);

    /*
     * Snippet load hone ke baad owner profile load hogi.
     */
    useEffect(() => {
        loadOwnerProfile();
    }, [
        loadOwnerProfile,
    ]);

    const showTemporaryMessage = (
        message
    ) => {

        setSuccessMessage(message);

        window.setTimeout(() => {
            setSuccessMessage("");
        }, 2500);
    };

    const requireLogin = (
        message
    ) => {

        if (token) {
            return true;
        }

        navigate("/login", {
            state: {
                message,
                redirectTo:
                    `/snippets/${snippetId}`,
            },
        });

        return false;
    };

    const handleCopyCode = async () => {

    try {

        let codeToCopy = "";

        if (
            Array.isArray(snippet.files) &&
            snippet.files.length > 0
        ) {

            codeToCopy = snippet.files
                .map(file =>
`// ${file.filename}

${file.code}`)
                .join("\n\n\n");

        } else {

            codeToCopy = snippet.code || "";
        }

        await navigator.clipboard.writeText(
            codeToCopy
        );

        setCopySuccess(true);

        setTimeout(() => {
            setCopySuccess(false);
        }, 1600);

    } catch {

        setErrorMessage(
            "Unable to copy code."
        );

    }

};

    const handleLikeToggle =
        async () => {

            if (
                !requireLogin(
                    "Please login to like snippets."
                )
            ) {
                return;
            }

            if (actionLoading) {
                return;
            }

            try {
                setActionLoading("LIKE");
                setErrorMessage("");

                const response =
                    liked
                        ? await unlikeSnippet(
                            snippetId
                        )
                        : await likeSnippet(
                            snippetId
                        );

                setLiked(
                    Boolean(
                        response?.liked
                    )
                );

                setSnippet(
                    (previousSnippet) => ({
                        ...previousSnippet,

                        likeCount:
                            Number(
                                response?.likeCount
                            ) || 0,
                    })
                );

                showTemporaryMessage(
                    response?.message ||
                    "Like status updated."
                );

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to update like."
                );

            } finally {
                setActionLoading("");
            }
        };

    const handleBookmarkToggle =
        async () => {

            if (
                !requireLogin(
                    "Please login to bookmark snippets."
                )
            ) {
                return;
            }

            if (actionLoading) {
                return;
            }

            try {
                setActionLoading(
                    "BOOKMARK"
                );

                setErrorMessage("");

                const response =
                    bookmarked
                        ? await removeSnippetBookmark(
                            snippetId
                        )
                        : await bookmarkSnippet(
                            snippetId
                        );

                setBookmarked(
                    Boolean(
                        response?.bookmarked
                    )
                );

                setSnippet(
                    (previousSnippet) => ({
                        ...previousSnippet,

                        bookmarkCount:
                            Number(
                                response
                                    ?.bookmarkCount
                            ) || 0,
                    })
                );

                showTemporaryMessage(
                    response?.message ||
                    "Bookmark status updated."
                );

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to update bookmark."
                );

            } finally {
                setActionLoading("");
            }
        };

    const handleFork =
        async () => {

            if (
                !requireLogin(
                    "Please login to fork snippets."
                )
            ) {
                return;
            }

            if (actionLoading) {
                return;
            }

            try {
                setActionLoading("FORK");
                setErrorMessage("");

                const response =
                    await forkSnippet(
                        snippetId
                    );

                setSnippet(
                    (previousSnippet) => ({
                        ...previousSnippet,

                        forkCount:
                            Number(
                                previousSnippet
                                    ?.forkCount
                            ) + 1,
                    })
                );

                showTemporaryMessage(
                    "Snippet forked successfully."
                );

                if (response?.snippetId) {
                    window.setTimeout(() => {
                        navigate(
                            `/snippets/${response.snippetId}`
                        );
                    }, 700);
                }

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to fork snippet."
                );

            } finally {
                setActionLoading("");
            }
        };

    const handleAddComment =
        async (event) => {

            event.preventDefault();

            if (
                !requireLogin(
                    "Please login to add comments."
                )
            ) {
                return;
            }

            const normalizedContent =
                commentText.trim();

            if (!normalizedContent) {
                setErrorMessage(
                    "Comment cannot be empty."
                );

                return;
            }

            try {
                setActionLoading(
                    "COMMENT"
                );

                setErrorMessage("");

                const response =
                    await addSnippetComment(
                        snippetId,
                        normalizedContent
                    );

                setComments(
                    (previousComments) => [
                        ...previousComments,
                        response,
                    ]
                );

                setSnippet(
                    (previousSnippet) => ({
                        ...previousSnippet,

                        commentCount:
                            Number(
                                previousSnippet
                                    ?.commentCount
                            ) + 1,
                    })
                );

                setCommentText("");

                showTemporaryMessage(
                    response?.message ||
                    "Comment added successfully."
                );

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to add comment."
                );

            } finally {
                setActionLoading("");
            }
        };

    const handleLoadReplies =
        async (commentId) => {

            const currentlyExpanded =
                Boolean(
                    expandedReplies[
                        commentId
                    ]
                );

            if (currentlyExpanded) {
                setExpandedReplies(
                    (previousState) => ({
                        ...previousState,
                        [commentId]: false,
                    })
                );

                return;
            }

            try {
                setActionLoading(
                    `REPLIES-${commentId}`
                );

                const response =
                    await getCommentReplies(
                        commentId
                    );

                setReplyMap(
                    (previousState) => ({
                        ...previousState,
                        [commentId]:
                            Array.isArray(
                                response
                            )
                                ? response
                                : [],
                    })
                );

                setExpandedReplies(
                    (previousState) => ({
                        ...previousState,
                        [commentId]: true,
                    })
                );

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to load replies."
                );

            } finally {
                setActionLoading("");
            }
        };

    const handleReply =
        async (
            event,
            commentId
        ) => {

            event.preventDefault();

            if (
                !requireLogin(
                    "Please login to reply."
                )
            ) {
                return;
            }

            const content =
                replyText[
                    commentId
                ]?.trim();

            if (!content) {
                setErrorMessage(
                    "Reply cannot be empty."
                );

                return;
            }

            try {
                setActionLoading(
                    `REPLY-${commentId}`
                );

                const response =
                    await replyToSnippetComment(
                        commentId,
                        content
                    );

                setReplyMap(
                    (previousState) => ({
                        ...previousState,

                        [commentId]: [
                            ...(
                                previousState[
                                    commentId
                                ] || []
                            ),
                            response,
                        ],
                    })
                );

                setExpandedReplies(
                    (previousState) => ({
                        ...previousState,
                        [commentId]: true,
                    })
                );

                setReplyText(
                    (previousState) => ({
                        ...previousState,
                        [commentId]: "",
                    })
                );

                setSnippet(
                    (previousSnippet) => ({
                        ...previousSnippet,

                        commentCount:
                            Number(
                                previousSnippet
                                    ?.commentCount
                            ) + 1,
                    })
                );

                showTemporaryMessage(
                    response?.message ||
                    "Reply added successfully."
                );

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to add reply."
                );

            } finally {
                setActionLoading("");
            }
        };

    const startEditingComment = (
        comment
    ) => {

        setEditingCommentId(
            comment.commentId
        );

        setEditingContent(
            comment.content || ""
        );
    };

    const cancelEditingComment =
        () => {

            setEditingCommentId(null);
            setEditingContent("");
        };

    const handleUpdateComment =
        async (
            event,
            commentId,
            parentCommentId = null
        ) => {

            event.preventDefault();

            const normalizedContent =
                editingContent.trim();

            if (!normalizedContent) {
                setErrorMessage(
                    "Comment cannot be empty."
                );

                return;
            }

            try {
                setActionLoading(
                    `EDIT-${commentId}`
                );

                const response =
                    await updateSnippetComment(
                        commentId,
                        normalizedContent
                    );

                if (parentCommentId) {

                    setReplyMap(
                        (previousState) => ({
                            ...previousState,

                            [parentCommentId]: (
                                previousState[
                                    parentCommentId
                                ] || []
                            ).map((reply) =>
                                reply.commentId ===
                                commentId
                                    ? response
                                    : reply
                            ),
                        })
                    );

                } else {

                    setComments(
                        (previousComments) =>
                            previousComments.map(
                                (comment) =>
                                    comment.commentId ===
                                    commentId
                                        ? response
                                        : comment
                            )
                    );
                }

                cancelEditingComment();

                showTemporaryMessage(
                    response?.message ||
                    "Comment updated successfully."
                );

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to update comment."
                );

            } finally {
                setActionLoading("");
            }
        };

    const handleDeleteComment =
        async (
            commentId,
            parentCommentId = null
        ) => {

            if (
                !window.confirm(
                    "Delete this comment?"
                )
            ) {
                return;
            }

            try {
                setActionLoading(
                    `DELETE-${commentId}`
                );

                const response =
                    await deleteSnippetComment(
                        commentId
                    );

                if (parentCommentId) {

                    setReplyMap(
                        (previousState) => ({
                            ...previousState,

                            [parentCommentId]: (
                                previousState[
                                    parentCommentId
                                ] || []
                            ).filter(
                                (reply) =>
                                    reply.commentId !==
                                    commentId
                            ),
                        })
                    );

                } else {

                    setComments(
                        (previousComments) =>
                            previousComments.filter(
                                (comment) =>
                                    comment.commentId !==
                                    commentId
                            )
                    );
                }

                setSnippet(
                    (previousSnippet) => ({
                        ...previousSnippet,

                        commentCount:
                            Math.max(
                                0,
                                Number(
                                    previousSnippet
                                        ?.commentCount
                                ) - 1
                            ),
                    })
                );

                showTemporaryMessage(
                    response?.message ||
                    "Comment deleted successfully."
                );

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to delete comment."
                );

            } finally {
                setActionLoading("");
            }
        };

    if (isLoading) {
        return (
            <main className="snippetDetailsPage">
                <div className="snippetDetailsLoading">
                    <span />

                    <p>
                        Loading snippet...
                    </p>
                </div>
            </main>
        );
    }

    if (!snippet) {
        return (
            <main className="snippetDetailsPage">

                <div className="snippetDetailsErrorState">

                    <h1>
                        Unable to open snippet
                    </h1>

                    <p>
                        {errorMessage ||
                        "Snippet was not found."}
                    </p>

                    <Link to="/snippets">
                        Back to snippets
                    </Link>

                </div>

            </main>
        );
    }

    return (
        <main className="snippetDetailsPage">

            <div className="snippetDetailsContainer">

                <button
                    type="button"
                    className="snippetDetailsBackButton"
                    onClick={() =>
                        navigate(-1)
                    }
                >
                    <FaArrowLeft />
                    Back
                </button>

                {errorMessage && (
                    <div className="snippetDetailsAlert snippetDetailsErrorAlert">
                        {errorMessage}
                    </div>
                )}

                {successMessage && (
                    <div className="snippetDetailsAlert snippetDetailsSuccessAlert">
                        {successMessage}
                    </div>
                )}

                <section className="snippetDetailsHeader">

                    <div className="snippetDetailsHeading">

                        {/*
                         * Instagram-style snippet owner.
                         *
                         * Owner profile available hai to clickable link.
                         * Click -> /users/{username}
                         */}
                        <SnippetOwner
                            ownerProfile={
                                ownerProfile
                            }
                            ownerLoading={
                                ownerLoading
                            }
                            fallbackUserId={
                                snippet.userId
                            }
                        />

                        <div className="snippetDetailsBadges">

                            <span>
                                <FaCode />
                                {snippet.language}
                            </span>

                            {snippet.framework && (
                                <span>
                                    {
                                        snippet.framework
                                    }
                                </span>
                            )}

                            <span
                                className={`snippetDetailsVisibility snippetDetailsVisibility${snippet.visibility}`}
                            >
                                {snippet.visibility ===
                                "PRIVATE" ? (
                                    <FaLock />
                                ) : (
                                    <FaEye />
                                )}

                                {
                                    snippet.visibility
                                }
                            </span>

                        </div>

                        <h1>
                            {snippet.title}
                        </h1>

                        <p>
                            {snippet.description}
                        </p>

                        <div className="snippetDetailsMeta">

                            <span>
                                Category:{" "}
                                <strong>
                                    {
                                        snippet.categoryName
                                    }
                                </strong>
                            </span>

                            <span>
                                Created:{" "}
                                <strong>
                                    {formatDate(
                                        snippet.createdAt
                                    )}
                                </strong>
                            </span>

                        </div>

                    </div>

                    {isOwner && (
                        <Link
                            to={`/snippets/${snippetId}/edit`}
                            className="snippetDetailsEditButton"
                        >
                            <FaEdit />
                            Edit snippet
                        </Link>
                    )}

                </section>

                {snippet.previewImageUrl && (
                    <section className="snippetDetailsImageSection">

                        <img
                            src={
                                snippet.previewImageUrl
                            }
                            alt={`${snippet.title} preview`}
                        />

                    </section>
                )}

                <section className="snippetDetailsCodeSection">

    <div className="snippetDetailsCodeHeader">

        <div>

            <FaCode />

            <span>
                {snippet.language}
            </span>

        </div>

        <button
            type="button"
            onClick={handleCopyCode}
        >
            <FaCopy />

            {copySuccess
                ? "Copied"
                : "Copy all"}
        </button>

    </div>

    {Array.isArray(snippet.files) &&
    snippet.files.length > 0 ? (

        snippet.files.map((file) => (

            <div
                key={file.fileOrder}
                className="snippetDetailsSingleFile"
            >

                <div className="snippetDetailsFileHeader">

                    📄 {file.filename}

                </div>

                <pre>

                    <code>

                        {file.code}

                    </code>

                </pre>

            </div>

        ))

    ) : (

        <pre>

            <code>

                {snippet.code}

            </code>

        </pre>

    )}

</section>

                {Array.isArray(
                    snippet.tags
                ) &&
                    snippet.tags.length >
                    0 && (
                    <section className="snippetDetailsTags">

                        {snippet.tags.map(
                            (tag) => (
                                <span key={tag}>
                                    #{tag}
                                </span>
                            )
                        )}

                    </section>
                )}

                <section className="snippetDetailsEngagement">

                    <div className="snippetDetailsStats">

                        <span>
                            <FaEye />
                            {
                                snippet.viewCount
                            }{" "}
                            views
                        </span>

                        <span>
                            <FaHeart />
                            {
                                snippet.likeCount
                            }{" "}
                            likes
                        </span>

                        <span>
                            <FaBookmark />
                            {
                                snippet.bookmarkCount
                            }{" "}
                            bookmarks
                        </span>

                        <span>
                            <FaShareAlt />
                            {
                                snippet.forkCount
                            }{" "}
                            forks
                        </span>

                        <span>
                            <FaComment />
                            {
                                snippet.commentCount
                            }{" "}
                            comments
                        </span>

                    </div>

                    <div className="snippetDetailsActions">

                        <button
                            type="button"
                            className={
                                liked
                                    ? "snippetDetailsActionActive"
                                    : ""
                            }
                            onClick={
                                handleLikeToggle
                            }
                            disabled={
                                Boolean(
                                    actionLoading
                                )
                            }
                        >
                            <FaHeart />

                            {liked
                                ? "Liked"
                                : "Like"}
                        </button>

                        <button
                            type="button"
                            className={
                                bookmarked
                                    ? "snippetDetailsBookmarkActive"
                                    : ""
                            }
                            onClick={
                                handleBookmarkToggle
                            }
                            disabled={
                                Boolean(
                                    actionLoading
                                )
                            }
                        >
                            <FaBookmark />

                            {bookmarked
                                ? "Saved"
                                : "Bookmark"}
                        </button>

                        {!isOwner && (
                            <button
                                type="button"
                                onClick={
                                    handleFork
                                }
                                disabled={
                                    Boolean(
                                        actionLoading
                                    )
                                }
                            >
                                <FaShareAlt />
                                Fork
                            </button>
                        )}

                    </div>

                </section>

                <section className="snippetDetailsComments">

                    <div className="snippetDetailsCommentsHeader">

                        <div>
                            <h2>
                                Comments
                            </h2>

                            <p>
                                Discuss this snippet
                                with the community.
                            </p>
                        </div>

                        <span>
                            {
                                snippet.commentCount
                            }
                        </span>

                    </div>

                    <form
                        className="snippetDetailsCommentForm"
                        onSubmit={
                            handleAddComment
                        }
                    >

                        <textarea
                            value={commentText}
                            onChange={(event) =>
                                setCommentText(
                                    event.target
                                        .value
                                )
                            }
                            placeholder="Write a comment..."
                            maxLength={1000}
                            rows={4}
                        />

                        <div>
                            <small>
                                {
                                    commentText.length
                                }
                                /1000
                            </small>

                            <button
                                type="submit"
                                disabled={
                                    actionLoading ===
                                    "COMMENT"
                                }
                            >
                                <FaComment />
                                Add comment
                            </button>
                        </div>

                    </form>

                    {commentsLoading ? (
                        <div className="snippetDetailsCommentsLoading">
                            Loading comments...
                        </div>
                    ) : comments.length ===
                      0 ? (
                        <div className="snippetDetailsNoComments">
                            No comments yet.
                        </div>
                    ) : (
                        <div className="snippetDetailsCommentList">

                            {comments.map(
                                (comment) => (
                                    <CommentItem
                                        key={
                                            comment.commentId
                                        }
                                        comment={
                                            comment
                                        }
                                        currentUserId={
                                            currentUserId
                                        }
                                        replies={
                                            replyMap[
                                                comment
                                                    .commentId
                                            ] || []
                                        }
                                        repliesExpanded={
                                            Boolean(
                                                expandedReplies[
                                                    comment
                                                        .commentId
                                                ]
                                            )
                                        }
                                        replyValue={
                                            replyText[
                                                comment
                                                    .commentId
                                            ] || ""
                                        }
                                        editingCommentId={
                                            editingCommentId
                                        }
                                        editingContent={
                                            editingContent
                                        }
                                        actionLoading={
                                            actionLoading
                                        }
                                        onLoadReplies={
                                            handleLoadReplies
                                        }
                                        onReplyChange={(
                                            value
                                        ) =>
                                            setReplyText(
                                                (
                                                    previousState
                                                ) => ({
                                                    ...previousState,

                                                    [comment.commentId]:
                                                        value,
                                                })
                                            )
                                        }
                                        onReply={
                                            handleReply
                                        }
                                        onStartEdit={
                                            startEditingComment
                                        }
                                        onCancelEdit={
                                            cancelEditingComment
                                        }
                                        onEditingContentChange={
                                            setEditingContent
                                        }
                                        onUpdateComment={
                                            handleUpdateComment
                                        }
                                        onDeleteComment={
                                            handleDeleteComment
                                        }
                                    />
                                )
                            )}

                        </div>
                    )}

                </section>

            </div>

        </main>
    );
}

/*
 * Snippet owner profile component.
 */
function SnippetOwner({
    ownerProfile,
    ownerLoading,
    fallbackUserId,
}) {

    if (ownerLoading) {
        return (
            <div className="snippetDetailsOwnerLoading">

                <span />

                <div>
                    <strong />
                    <small />
                </div>

            </div>
        );
    }

    const ownerName =
        ownerProfile?.fullName ||
        "Unknown user";

    const ownerUsername =
        ownerProfile?.username ||
        "";

    const ownerImage =
        ownerProfile?.profileImage ||
        null;

    const ownerContent = (
        <>
            <div className="snippetDetailsOwnerAvatar">

                {ownerImage ? (
                    <img
                        src={ownerImage}
                        alt={ownerName}
                    />
                ) : (
                    <FaUser />
                )}

            </div>

            <div className="snippetDetailsOwnerIdentity">

                <strong>
                    {ownerName}
                </strong>

                <small>
                    {ownerUsername
                        ? `@${ownerUsername}`
                        : fallbackUserId
                            ? `User ${String(
                                fallbackUserId
                            ).slice(0, 8)}`
                            : "Unknown account"}
                </small>

            </div>

            {ownerUsername && (
                <span className="snippetDetailsOwnerView">
                    View profile
                </span>
            )}
        </>
    );

    if (ownerUsername) {
        return (
            <Link
                to={`/users/${encodeURIComponent(
                    ownerUsername
                )}`}
                className="snippetDetailsOwner"
            >
                {ownerContent}
            </Link>
        );
    }

    return (
        <div className="snippetDetailsOwner snippetDetailsOwnerFallback">
            {ownerContent}
        </div>
    );
}

function CommentItem({
    comment,
    currentUserId,
    replies,
    repliesExpanded,
    replyValue,
    editingCommentId,
    editingContent,
    actionLoading,
    onLoadReplies,
    onReplyChange,
    onReply,
    onStartEdit,
    onCancelEdit,
    onEditingContentChange,
    onUpdateComment,
    onDeleteComment,
}) {

    const isOwner =
        Boolean(
            currentUserId &&
            comment.userId ===
            currentUserId
        );

    const isEditing =
        editingCommentId ===
        comment.commentId;

    return (
        <article className="snippetDetailsComment">

            <div className="snippetDetailsCommentTop">

                <div className="snippetDetailsCommentAuthor">

                    <span>
                        <FaUser />
                    </span>

                    <div>
                        <strong>
                            {comment.userId
                                ? comment.userId.slice(
                                    0,
                                    8
                                )
                                : "Unknown user"}
                        </strong>

                        <small>
                            {formatDate(
                                comment.createdAt
                            )}
                        </small>
                    </div>

                </div>

                {isOwner && (
                    <div className="snippetDetailsCommentOwnerActions">

                        <button
                            type="button"
                            onClick={() =>
                                onStartEdit(
                                    comment
                                )
                            }
                        >
                            <FaEdit />
                        </button>

                        <button
                            type="button"
                            onClick={() =>
                                onDeleteComment(
                                    comment.commentId
                                )
                            }
                        >
                            <FaTrash />
                        </button>

                    </div>
                )}

            </div>

            {isEditing ? (
                <form
                    className="snippetDetailsEditCommentForm"
                    onSubmit={(event) =>
                        onUpdateComment(
                            event,
                            comment.commentId
                        )
                    }
                >

                    <textarea
                        value={
                            editingContent
                        }
                        onChange={(event) =>
                            onEditingContentChange(
                                event.target
                                    .value
                            )
                        }
                        rows={3}
                    />

                    <div>
                        <button
                            type="button"
                            onClick={
                                onCancelEdit
                            }
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                        >
                            Save
                        </button>
                    </div>

                </form>
            ) : (
                <p>
                    {comment.content}
                </p>
            )}

            <div className="snippetDetailsCommentBottom">

                <button
                    type="button"
                    onClick={() =>
                        onLoadReplies(
                            comment.commentId
                        )
                    }
                >
                    <FaReply />

                    {repliesExpanded
                        ? "Hide replies"
                        : "View replies"}
                </button>

            </div>

            {repliesExpanded && (
                <div className="snippetDetailsReplies">

                    {replies.map(
                        (reply) => (
                            <ReplyItem
                                key={
                                    reply.commentId
                                }
                                reply={reply}
                                parentCommentId={
                                    comment.commentId
                                }
                                currentUserId={
                                    currentUserId
                                }
                                editingCommentId={
                                    editingCommentId
                                }
                                editingContent={
                                    editingContent
                                }
                                onStartEdit={
                                    onStartEdit
                                }
                                onCancelEdit={
                                    onCancelEdit
                                }
                                onEditingContentChange={
                                    onEditingContentChange
                                }
                                onUpdateComment={
                                    onUpdateComment
                                }
                                onDeleteComment={
                                    onDeleteComment
                                }
                            />
                        )
                    )}

                    <form
                        className="snippetDetailsReplyForm"
                        onSubmit={(event) =>
                            onReply(
                                event,
                                comment.commentId
                            )
                        }
                    >

                        <input
                            type="text"
                            value={replyValue}
                            onChange={(event) =>
                                onReplyChange(
                                    event.target
                                        .value
                                )
                            }
                            placeholder="Write a reply..."
                            maxLength={1000}
                        />

                        <button
                            type="submit"
                            disabled={
                                actionLoading ===
                                `REPLY-${comment.commentId}`
                            }
                        >
                            <FaReply />
                            Reply
                        </button>

                    </form>

                </div>
            )}

        </article>
    );
}

function ReplyItem({
    reply,
    parentCommentId,
    currentUserId,
    editingCommentId,
    editingContent,
    onStartEdit,
    onCancelEdit,
    onEditingContentChange,
    onUpdateComment,
    onDeleteComment,
}) {

    const isOwner =
        Boolean(
            currentUserId &&
            reply.userId ===
            currentUserId
        );

    const isEditing =
        editingCommentId ===
        reply.commentId;

    return (
        <article className="snippetDetailsReply">

            <div>

                <span className="snippetDetailsReplyAvatar">
                    <FaUser />
                </span>

                <div className="snippetDetailsReplyContent">

                    <div className="snippetDetailsReplyHeader">

                        <div>
                            <strong>
                                {reply.userId
                                    ? reply.userId.slice(
                                        0,
                                        8
                                    )
                                    : "Unknown user"}
                            </strong>

                            <small>
                                {formatDate(
                                    reply.createdAt
                                )}
                            </small>
                        </div>

                        {isOwner && (
                            <div>

                                <button
                                    type="button"
                                    onClick={() =>
                                        onStartEdit(
                                            reply
                                        )
                                    }
                                >
                                    <FaEdit />
                                </button>

                                <button
                                    type="button"
                                    onClick={() =>
                                        onDeleteComment(
                                            reply.commentId,
                                            parentCommentId
                                        )
                                    }
                                >
                                    <FaTrash />
                                </button>

                            </div>
                        )}

                    </div>

                    {isEditing ? (
                        <form
                            className="snippetDetailsEditCommentForm"
                            onSubmit={(event) =>
                                onUpdateComment(
                                    event,
                                    reply.commentId,
                                    parentCommentId
                                )
                            }
                        >

                            <textarea
                                value={
                                    editingContent
                                }
                                onChange={(event) =>
                                    onEditingContentChange(
                                        event.target
                                            .value
                                    )
                                }
                                rows={3}
                            />

                            <div>
                                <button
                                    type="button"
                                    onClick={
                                        onCancelEdit
                                    }
                                >
                                    Cancel
                                </button>

                                <button
                                    type="submit"
                                >
                                    Save
                                </button>
                            </div>

                        </form>
                    ) : (
                        <p>
                            {reply.content}
                        </p>
                    )}

                </div>

            </div>

        </article>
    );
}

function formatDate(dateValue) {

    if (!dateValue) {
        return "Unknown date";
    }

    const date =
        new Date(dateValue);

    if (
        Number.isNaN(
            date.getTime()
        )
    ) {
        return "Unknown date";
    }

    return new Intl.DateTimeFormat(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        }
    ).format(date);
}

export default SnippetDetails;