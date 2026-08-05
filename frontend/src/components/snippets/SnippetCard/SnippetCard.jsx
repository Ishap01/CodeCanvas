import React, {
    useEffect,
    useMemo,
    useState,
} from "react";

import {
    Link,
    useNavigate,
} from "react-router-dom";

import {
    FaBookmark,
    FaCode,
    FaComment,
    FaEllipsisV,
    FaEye,
    FaHeart,
    FaLock,
    FaRegBookmark,
    FaRegHeart,
    FaShareAlt,
    FaTrash,
    FaUser,
} from "react-icons/fa";

import {
    bookmarkSnippet,
    forkSnippet,
    likeSnippet,
    removeSnippetBookmark,
    unlikeSnippet,
} from "../../../services/snippetService";

import SnippetCodeModal from "../SnippetCodeModal/SnippetCodeModal";

import "./SnippetCard.css";

function SnippetCard({
    snippet,
    showOwnerActions = false,
    deleting = false,
    onDelete,

    showBookmarkAction = false,
    isBookmarked = false,
    onBookmarkToggle,
}) {

    const navigate = useNavigate();

    const token =
        localStorage.getItem("token");

    const [codeModalOpen, setCodeModalOpen] =
        useState(false);

    const [menuOpen, setMenuOpen] =
        useState(false);

    const [liked, setLiked] =
        useState(
            Boolean(snippet?.liked)
        );

    const [bookmarked, setBookmarked] =
        useState(
            Boolean(
                isBookmarked ||
                snippet?.bookmarked
            )
        );

    const [likeCount, setLikeCount] =
        useState(
            Number(
                snippet?.likeCount
            ) || 0
        );

    const [
        bookmarkCount,
        setBookmarkCount,
    ] = useState(
        Number(
            snippet?.bookmarkCount
        ) || 0
    );

    const [forkCount, setForkCount] =
        useState(
            Number(
                snippet?.forkCount
            ) || 0
        );

    const [actionLoading, setActionLoading] =
        useState("");

    const tags = useMemo(() => {

        if (!Array.isArray(snippet?.tags)) {
            return [];
        }

        return snippet.tags.slice(0, 4);

    }, [snippet?.tags]);

    useEffect(() => {

        setLiked(
            Boolean(snippet?.liked)
        );

        setBookmarked(
            Boolean(
                isBookmarked ||
                snippet?.bookmarked
            )
        );

        setLikeCount(
            Number(
                snippet?.likeCount
            ) || 0
        );

        setBookmarkCount(
            Number(
                snippet?.bookmarkCount
            ) || 0
        );

        setForkCount(
            Number(
                snippet?.forkCount
            ) || 0
        );

    }, [
        snippet,
        isBookmarked,
    ]);

    if (!snippet) {
        return null;
    }

    const requireLogin = () => {

        if (token) {
            return true;
        }

        navigate("/login");

        return false;
    };

    const handleLike = async () => {

        if (
            !requireLogin() ||
            actionLoading
        ) {
            return;
        }

        try {
            setActionLoading("LIKE");

            const response =
                liked
                    ? await unlikeSnippet(
                        snippet.snippetId
                    )
                    : await likeSnippet(
                        snippet.snippetId
                    );

            setLiked(
                Boolean(response?.liked)
            );

            setLikeCount(
                Number(
                    response?.likeCount
                ) || 0
            );

        } catch (error) {

            console.error(
                "Like request failed:",
                error
            );

        } finally {
            setActionLoading("");
        }
    };

    const handleBookmark = async () => {

        if (
            !requireLogin() ||
            actionLoading
        ) {
            return;
        }

        if (onBookmarkToggle) {

            try {
                setActionLoading(
                    "BOOKMARK"
                );

                await onBookmarkToggle(
                    snippet
                );

                setBookmarked(false);

            } finally {
                setActionLoading("");
            }

            return;
        }

        try {
            setActionLoading(
                "BOOKMARK"
            );

            const response =
                bookmarked
                    ? await removeSnippetBookmark(
                        snippet.snippetId
                    )
                    : await bookmarkSnippet(
                        snippet.snippetId
                    );

            setBookmarked(
                Boolean(
                    response?.bookmarked
                )
            );

            setBookmarkCount(
                Number(
                    response?.bookmarkCount
                ) || 0
            );

        } catch (error) {

            console.error(
                "Bookmark request failed:",
                error
            );

        } finally {
            setActionLoading("");
        }
    };

    const handleFork = async () => {

        if (
            !requireLogin() ||
            actionLoading
        ) {
            return;
        }

        try {
            setActionLoading("FORK");

            const response =
                await forkSnippet(
                    snippet.snippetId
                );

            setForkCount(
                (previousCount) =>
                    previousCount + 1
            );

            if (response?.snippetId) {
                navigate(
                    `/snippets/${response.snippetId}`
                );
            }

        } catch (error) {

            console.error(
                "Fork request failed:",
                error
            );

        } finally {
            setActionLoading("");
        }
    };

    const handleDelete = () => {

        setMenuOpen(false);

        if (onDelete) {
            onDelete(snippet);
        }
    };

    const previewStyle =
        snippet.previewImageUrl
            ? {
                backgroundImage:
                    `url("${snippet.previewImageUrl}")`,
            }
            : undefined;

    return (
        <>
            <article className="instagramSnippetCard">

                <header className="instagramSnippetHeader">

                    <Link
                        to={`/snippets/${snippet.snippetId}`}
                        className="instagramSnippetIdentity"
                    >

                        <span className="instagramSnippetAvatar">
                            <FaUser />
                        </span>

                        <div>

                            <strong>
                                {snippet.title}
                            </strong>

                            <span>
                                {snippet.language ||
                                "Code"}

                                {snippet.framework
                                    ? ` • ${snippet.framework}`
                                    : ""}
                            </span>

                        </div>

                    </Link>

                    <div className="instagramSnippetHeaderRight">

                        <span
                            className={`instagramSnippetVisibility instagramSnippetVisibility${snippet.visibility}`}
                        >
                            {snippet.visibility ===
                            "PRIVATE" ? (
                                <FaLock />
                            ) : (
                                <FaEye />
                            )}

                            {snippet.visibility}
                        </span>

                        {showOwnerActions && (
                            <div className="instagramSnippetMenu">

                                <button
                                    type="button"
                                    className="instagramSnippetMenuTrigger"
                                    onClick={() =>
                                        setMenuOpen(
                                            (
                                                previous
                                            ) =>
                                                !previous
                                        )
                                    }
                                >
                                    <FaEllipsisV />
                                </button>

                                {menuOpen && (
                                    <div className="instagramSnippetMenuDropdown">

                                        <Link
                                            to={`/snippets/${snippet.snippetId}/edit`}
                                            onClick={() =>
                                                setMenuOpen(
                                                    false
                                                )
                                            }
                                        >
                                            Edit snippet
                                        </Link>

                                        <button
                                            type="button"
                                            onClick={
                                                handleDelete
                                            }
                                            disabled={
                                                deleting
                                            }
                                        >
                                            <FaTrash />

                                            Delete
                                        </button>

                                    </div>
                                )}

                            </div>
                        )}

                    </div>

                </header>

                <button
                    type="button"
                    className={`instagramSnippetPreview ${
                        snippet.previewImageUrl
                            ? "instagramSnippetHasImage"
                            : "instagramSnippetCodePreview"
                    }`}
                    style={previewStyle}
                    onClick={() =>
                        setCodeModalOpen(true)
                    }
                    aria-label={`Open code for ${snippet.title}`}
                >

                    {!snippet.previewImageUrl && (
                        <>
                            <span className="instagramSnippetCodeIcon">
                                <FaCode />
                            </span>

                            <pre>
                                <code>
                                    {createCodePreview(
                                        snippet.code
                                    )}
                                </code>
                            </pre>
                        </>
                    )}

                    <span className="instagramSnippetPreviewOverlay">
                        <FaCode />

                        Click to view code
                    </span>

                </button>

                <div className="instagramSnippetActions">

                    <div className="instagramSnippetPrimaryActions">

                        <button
                            type="button"
                            className={
                                liked
                                    ? "instagramSnippetLiked"
                                    : ""
                            }
                            onClick={handleLike}
                            disabled={
                                actionLoading ===
                                "LIKE"
                            }
                            aria-label="Like snippet"
                        >
                            {liked ? (
                                <FaHeart />
                            ) : (
                                <FaRegHeart />
                            )}
                        </button>

                        <button
                            type="button"
                            onClick={() =>
                                navigate(
                                    `/snippets/${snippet.snippetId}#comments`
                                )
                            }
                            aria-label="Open comments"
                        >
                            <FaComment />
                        </button>

                        <button
                            type="button"
                            onClick={handleFork}
                            disabled={
                                actionLoading ===
                                "FORK"
                            }
                            aria-label="Fork snippet"
                        >
                            <FaShareAlt />
                        </button>

                    </div>

                    <button
                        type="button"
                        className={`instagramSnippetBookmarkButton ${
                            bookmarked
                                ? "instagramSnippetBookmarked"
                                : ""
                        }`}
                        onClick={handleBookmark}
                        disabled={
                            actionLoading ===
                            "BOOKMARK"
                        }
                        aria-label="Bookmark snippet"
                    >
                        {bookmarked ? (
                            <FaBookmark />
                        ) : (
                            <FaRegBookmark />
                        )}
                    </button>

                </div>

                <div className="instagramSnippetCounts">

                    <span>
                        <strong>
                            {formatCount(
                                likeCount
                            )}
                        </strong>{" "}
                        likes
                    </span>

                    <span>
                        <strong>
                            {formatCount(
                                snippet.commentCount
                            )}
                        </strong>{" "}
                        comments
                    </span>

                    <span>
                        <strong>
                            {formatCount(
                                forkCount
                            )}
                        </strong>{" "}
                        forks
                    </span>

                    <span>
                        <strong>
                            {formatCount(
                                bookmarkCount
                            )}
                        </strong>{" "}
                        saves
                    </span>

                </div>

                <div className="instagramSnippetContent">

                    <p className="instagramSnippetDescription">
                        <strong>
                            {snippet.language}
                        </strong>{" "}

                        {snippet.description}
                    </p>

                    {tags.length > 0 && (
                        <div className="instagramSnippetTags">

                            {tags.map((tag) => (
                                <span key={tag}>
                                    #{tag}
                                </span>
                            ))}

                            {snippet.tags.length >
                                tags.length && (
                                <span>
                                    +
                                    {
                                        snippet.tags
                                            .length -
                                        tags.length
                                    }
                                </span>
                            )}

                        </div>
                    )}

                    <Link
                        to={`/snippets/${snippet.snippetId}#comments`}
                        className="instagramSnippetViewComments"
                    >
                        View all{" "}
                        {Number(
                            snippet.commentCount
                        ) || 0}{" "}
                        comments
                    </Link>

                    <div className="instagramSnippetFooter">

                        <span>
                            <FaEye />

                            {formatCount(
                                snippet.viewCount
                            )}{" "}
                            views
                        </span>

                        <time>
                            {formatDate(
                                snippet.createdAt
                            )}
                        </time>

                    </div>

                </div>

                {deleting && (
                    <div className="instagramSnippetDeletingOverlay">
                        <span />

                        Deleting...
                    </div>
                )}

            </article>

            <SnippetCodeModal
                snippet={snippet}
                isOpen={codeModalOpen}
                onClose={() =>
                    setCodeModalOpen(false)
                }
            />
        </>
    );
}

function createCodePreview(code) {

    if (!code) {
        return "// Click to view code";
    }

    const lines =
        String(code)
            .split("\n")
            .slice(0, 10);

    return lines.join("\n");
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

    return String(number);
}

function formatDate(value) {

    if (!value) {
        return "";
    }

    const date =
        new Date(value);

    if (
        Number.isNaN(
            date.getTime()
        )
    ) {
        return "";
    }

    return new Intl.DateTimeFormat(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric",
        }
    ).format(date);
}

export default SnippetCard;