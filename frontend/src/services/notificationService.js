import axiosInstance from "../api/axios";

const NOTIFICATION_BASE_URL = "/api/notifications";

/*
 * Converts backend errors into readable messages.
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

        const validationMessages =
            Object.values(
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
 * GET ALL NOTIFICATIONS
 *
 * GET /api/notifications
 */
export const getNotifications = async () => {

    try {

        const response =
            await axiosInstance.get(
                NOTIFICATION_BASE_URL
            );

        return response.data;

    } catch (error) {

        throw new Error(
            extractErrorMessage(
                error,
                "Unable to load notifications."
            )
        );
    }
};

/*
 * GET UNREAD COUNT
 *
 * GET /api/notifications/unread-count
 */
export const getUnreadCount = async () => {

    try {

        const response =
            await axiosInstance.get(
                `${NOTIFICATION_BASE_URL}/unread-count`
            );

        return response.data;

    } catch (error) {

        throw new Error(
            extractErrorMessage(
                error,
                "Unable to load unread notifications."
            )
        );
    }
};

/*
 * MARK NOTIFICATION AS READ
 *
 * PUT /api/notifications/{notificationId}/read
 */
export const markAsRead = async (
    notificationId
) => {

    try {

        const response =
            await axiosInstance.put(
                `${NOTIFICATION_BASE_URL}/${notificationId}/read`
            );

        return response.data;

    } catch (error) {

        throw new Error(
            extractErrorMessage(
                error,
                "Unable to mark notification as read."
            )
        );
    }
};

/*
 * DELETE NOTIFICATION
 *
 * DELETE /api/notifications/{notificationId}
 */
export const deleteNotification = async (
    notificationId
) => {

    try {

        const response =
            await axiosInstance.delete(
                `${NOTIFICATION_BASE_URL}/${notificationId}`
            );

        return response.data;

    } catch (error) {

        throw new Error(
            extractErrorMessage(
                error,
                "Unable to delete notification."
            )
        );
    }
};