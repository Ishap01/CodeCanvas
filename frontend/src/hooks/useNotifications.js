import {
    useCallback,
    useEffect,
    useState,
} from "react";

import {
    deleteNotification,
    getNotifications,
    getUnreadCount,
    markAsRead,
} from "../services/notificationService";

export default function useNotifications() {

    const [notifications, setNotifications] =
        useState([]);

    const [unreadCount, setUnreadCount] =
        useState(0);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    const loadNotifications =
        useCallback(async () => {

            try {

                setLoading(true);
                setError("");

                const [
                    notificationData,
                    unreadData,
                ] = await Promise.all([
                    getNotifications(),
                    getUnreadCount(),
                ]);

                setNotifications(
                    Array.isArray(notificationData)
                        ? notificationData
                        : []
                );

                setUnreadCount(
                    Number(unreadData) || 0
                );

            } catch (requestError) {

                console.error(
                    "Unable to load notifications:",
                    requestError
                );

                setError(
                    requestError.message ||
                    "Unable to load notifications."
                );

            } finally {

                setLoading(false);

            }

        }, []);

    useEffect(() => {

        loadNotifications();

    }, [loadNotifications]);

    const refreshNotifications =
        useCallback(() => {

            loadNotifications();

        }, [loadNotifications]);

    const markNotificationAsRead =
        useCallback(async (notificationId) => {

            try {

                await markAsRead(
                    notificationId
                );

                setNotifications(
                    previous =>
                        previous.map(
                            notification =>
                                notification.notificationId ===
                                    notificationId
                                    ? {
                                        ...notification,
                                        isRead: true,
                                    }
                                    : notification
                        )
                );

                setUnreadCount(previous =>
                    Math.max(previous - 1, 0)
                );

            } catch (error) {

                console.error(error);

            }

        }, []);

    const removeNotification =
        useCallback(async (notificationId) => {

            try {

                const notification =
                    notifications.find(
                        item =>
                            item.notificationId ===
                            notificationId
                    );

                await deleteNotification(
                    notificationId
                );

                setNotifications(
                    previous =>
                        previous.filter(
                            item =>
                                item.notificationId !==
                                notificationId
                        )
                );

                if (
                    notification &&
                    !notification.isRead
                ) {
                    setUnreadCount(previous =>
                        Math.max(previous - 1, 0)
                    );
                }

            } catch (error) {

                console.error(error);

            }

        }, [notifications]);

    return {

        notifications,

        unreadCount,

        loading,

        error,

        refreshNotifications,

        markNotificationAsRead,

        removeNotification,

    };
}