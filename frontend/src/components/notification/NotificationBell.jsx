import {
    useEffect,
    useRef,
    useState,
} from "react";

import "./NotificationBell.css";

import { FaBell } from "react-icons/fa";

import useNotifications from "../../hooks/useNotifications";

import NotificationDropdown from "./NotificationDropdown";

export default function NotificationBell() {

    const [isOpen, setIsOpen] =
        useState(false);

    const dropdownRef =
        useRef(null);

    const {

        notifications,

        unreadCount,

        loading,

        markNotificationAsRead,

        removeNotification,

    } = useNotifications();

    useEffect(() => {

        function handleOutsideClick(
            event
        ) {

            if (
                dropdownRef.current &&
                !dropdownRef.current.contains(
                    event.target
                )
            ) {

                setIsOpen(false);

            }

        }

        document.addEventListener(
            "mousedown",
            handleOutsideClick
        );

        return () =>
            document.removeEventListener(
                "mousedown",
                handleOutsideClick
            );

    }, []);

    return (

        <div
            className="notificationBell"
            ref={dropdownRef}
        >

            <button
                type="button"
                className="notificationBellButton"
                aria-label="Notifications"
                title="Notifications"
                onClick={() =>
                    setIsOpen(
                        previous => !previous
                    )
                }
            >

                <FaBell />

                {unreadCount > 0 && (

                    <span
                        className="notificationBadge"
                    >

                        {unreadCount > 99
                            ? "99+"
                            : unreadCount}

                    </span>

                )}

            </button>

            {isOpen && (

                <NotificationDropdown
                    notifications={
                        notifications
                    }
                    loading={loading}
                    onMarkAsRead={
                        markNotificationAsRead
                    }
                    onDelete={
                        removeNotification
                    }
                    onClose={() =>
                        setIsOpen(false)
                    }
                />

            )}

        </div>

    );

}