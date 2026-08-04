import axiosInstance from "../api/axios";

const SNIPPET_BASE_URL = "/api/snippets";

/*
 * Backend error ko readable message mein
 * convert karta hai.
 */
const extractErrorMessage = (
    error,
    fallbackMessage = "Something went wrong."
) => {
    const responseData = error?.response?.data;

    if (responseData?.message) {
        return responseData.message;
    }

    if (responseData?.validationErrors) {
        const validationMessages = Object.values(
            responseData.validationErrors
        );

        if (validationMessages.length > 0) {
            return validationMessages.join(", ");
        }
    }

    if (error?.message) {
        return error.message;
    }

    return fallbackMessage;
};

/*
 * CREATE SNIPPET
 *
 * POST /api/snippets
 */
export const createSnippet = async (snippetData) => {
    try {
        const response = await axiosInstance.post(
            SNIPPET_BASE_URL,
            snippetData
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to create snippet."
            )
        );
    }
};

/*
 * GET ALL PUBLIC SNIPPETS
 *
 * GET /api/snippets/public
 *
 * Ye original working endpoint hai.
 * Isko premium-aware endpoint se replace nahi kiya gaya.
 */
export const getPublicSnippets = async () => {
    try {
        const response = await axiosInstance.get(
            `${SNIPPET_BASE_URL}/public`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to load public snippets."
            )
        );
    }
};

/*
 * GET CURRENT USER SNIPPETS
 *
 * GET /api/snippets/user/me
 */
export const getMySnippets = async () => {
    try {
        const response = await axiosInstance.get(
            `${SNIPPET_BASE_URL}/user/me`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to load your snippets."
            )
        );
    }
};

/*
 * GET SINGLE SNIPPET
 *
 * GET /api/snippets/{snippetId}
 */
export const getSnippetById = async (snippetId) => {
    try {
        const response = await axiosInstance.get(
            `${SNIPPET_BASE_URL}/${snippetId}`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to load snippet."
            )
        );
    }
};

/*
 * UPDATE SNIPPET
 *
 * PUT /api/snippets/{snippetId}
 */
export const updateSnippet = async (
    snippetId,
    snippetData
) => {
    try {
        const response = await axiosInstance.put(
            `${SNIPPET_BASE_URL}/${snippetId}`,
            snippetData
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to update snippet."
            )
        );
    }
};

/*
 * DELETE SNIPPET
 *
 * DELETE /api/snippets/{snippetId}
 */
export const deleteSnippet = async (snippetId) => {
    try {
        const response = await axiosInstance.delete(
            `${SNIPPET_BASE_URL}/${snippetId}`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to delete snippet."
            )
        );
    }
};

/*
 * UPLOAD FIRST IMAGE
 *
 * POST /api/snippets/{snippetId}/image
 */
export const uploadSnippetImage = async (
    snippetId,
    imageFile
) => {
    try {
        const formData = new FormData();

        formData.append("image", imageFile);

        const response = await axiosInstance.post(
            `${SNIPPET_BASE_URL}/${snippetId}/image`,
            formData
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to upload snippet image."
            )
        );
    }
};

/*
 * REPLACE IMAGE
 *
 * PUT /api/snippets/{snippetId}/image
 */
export const replaceSnippetImage = async (
    snippetId,
    imageFile
) => {
    try {
        const formData = new FormData();

        formData.append("image", imageFile);

        const response = await axiosInstance.put(
            `${SNIPPET_BASE_URL}/${snippetId}/image`,
            formData
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to replace snippet image."
            )
        );
    }
};

/*
 * DELETE IMAGE
 *
 * DELETE /api/snippets/{snippetId}/image
 */
export const deleteSnippetImage = async (snippetId) => {
    try {
        const response = await axiosInstance.delete(
            `${SNIPPET_BASE_URL}/${snippetId}/image`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to delete snippet image."
            )
        );
    }
};

/*
 * LIKE SNIPPET
 *
 * POST /api/snippets/{snippetId}/like
 */
export const likeSnippet = async (snippetId) => {
    try {
        const response = await axiosInstance.post(
            `${SNIPPET_BASE_URL}/${snippetId}/like`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to like snippet."
            )
        );
    }
};

/*
 * REMOVE LIKE
 *
 * DELETE /api/snippets/{snippetId}/like
 */
export const unlikeSnippet = async (snippetId) => {
    try {
        const response = await axiosInstance.delete(
            `${SNIPPET_BASE_URL}/${snippetId}/like`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to remove like."
            )
        );
    }
};

/*
 * GET CURRENT USER LIKE STATUS
 *
 * GET /api/snippets/{snippetId}/liked
 */
export const getSnippetLikeStatus = async (snippetId) => {
    try {
        const response = await axiosInstance.get(
            `${SNIPPET_BASE_URL}/${snippetId}/liked`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to load like status."
            )
        );
    }
};

/*
 * BOOKMARK SNIPPET
 *
 * POST /api/snippets/{snippetId}/bookmark
 */
export const bookmarkSnippet = async (snippetId) => {
    try {
        const response = await axiosInstance.post(
            `${SNIPPET_BASE_URL}/${snippetId}/bookmark`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to bookmark snippet."
            )
        );
    }
};

/*
 * REMOVE BOOKMARK
 *
 * DELETE /api/snippets/{snippetId}/bookmark
 */
export const removeSnippetBookmark = async (
    snippetId
) => {
    try {
        const response = await axiosInstance.delete(
            `${SNIPPET_BASE_URL}/${snippetId}/bookmark`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to remove bookmark."
            )
        );
    }
};

/*
 * GET CURRENT USER BOOKMARK STATUS
 *
 * GET /api/snippets/{snippetId}/bookmarked
 */
export const getSnippetBookmarkStatus = async (
    snippetId
) => {
    try {
        const response = await axiosInstance.get(
            `${SNIPPET_BASE_URL}/${snippetId}/bookmarked`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to load bookmark status."
            )
        );
    }
};

/*
 * GET CURRENT USER BOOKMARKED SNIPPETS
 *
 * GET /api/snippets/bookmarks/me
 */
export const getMyBookmarkedSnippets = async () => {
    try {
        const response = await axiosInstance.get(
            `${SNIPPET_BASE_URL}/bookmarks/me`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to load bookmarked snippets."
            )
        );
    }
};

/*
 * FORK SNIPPET
 *
 * POST /api/snippets/{snippetId}/fork
 */
export const forkSnippet = async (snippetId) => {
    try {
        const response = await axiosInstance.post(
            `${SNIPPET_BASE_URL}/${snippetId}/fork`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to fork snippet."
            )
        );
    }
};

/*
 * GET TOP-LEVEL COMMENTS
 *
 * GET /api/snippets/{snippetId}/comments
 */
export const getSnippetComments = async (
    snippetId
) => {
    try {
        const response = await axiosInstance.get(
            `${SNIPPET_BASE_URL}/${snippetId}/comments`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to load comments."
            )
        );
    }
};

/*
 * ADD COMMENT
 *
 * POST /api/snippets/{snippetId}/comments
 */
export const addSnippetComment = async (
    snippetId,
    content
) => {
    try {
        const response = await axiosInstance.post(
            `${SNIPPET_BASE_URL}/${snippetId}/comments`,
            {
                content,
            }
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to add comment."
            )
        );
    }
};

/*
 * UPDATE COMMENT
 *
 * PUT /api/comments/{commentId}
 */
export const updateSnippetComment = async (
    commentId,
    content
) => {
    try {
        const response = await axiosInstance.put(
            `/api/comments/${commentId}`,
            {
                content,
            }
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to update comment."
            )
        );
    }
};

/*
 * DELETE COMMENT
 *
 * DELETE /api/comments/{commentId}
 */
export const deleteSnippetComment = async (
    commentId
) => {
    try {
        const response = await axiosInstance.delete(
            `/api/comments/${commentId}`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to delete comment."
            )
        );
    }
};

/*
 * REPLY TO COMMENT
 *
 * POST /api/comments/{commentId}/replies
 */
export const replyToSnippetComment = async (
    commentId,
    content
) => {
    try {
        const response = await axiosInstance.post(
            `/api/comments/${commentId}/replies`,
            {
                content,
            }
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to add reply."
            )
        );
    }
};

/*
 * GET COMMENT REPLIES
 *
 * GET /api/comments/{commentId}/replies
 */
export const getCommentReplies = async (
    commentId
) => {
    try {
        const response = await axiosInstance.get(
            `/api/comments/${commentId}/replies`
        );

        return response.data;
    } catch (error) {
        throw new Error(
            extractErrorMessage(
                error,
                "Unable to load replies."
            )
        );
    }
};