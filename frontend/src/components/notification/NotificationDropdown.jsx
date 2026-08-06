import "./NotificationDropdown.css";

import { Link } from "react-router-dom";

import NotificationItem from "./NotificationItem";

export default function NotificationDropdown({

    notifications,

    loading,

    onMarkAsRead,

    onDelete,

    onClose,

}) {

    if (loading) {

        return (

            <div className="notificationDropdown">

                <div className="notificationDropdownHeader">

                    Notifications

                </div>

                <div className="notificationDropdownBody">

                    <p className="notificationLoading">

                        Loading notifications...

                    </p>

                </div>

            </div>

        );

    }

    return (

        <div className="notificationDropdown">

            <div className="notificationDropdownHeader">

                <h3>Notifications</h3>

            </div>

            <div className="notificationDropdownBody">

                {notifications.length === 0 && (

                    <div className="notificationEmpty">

                        <h4>

                            You're all caught up 🎉

                        </h4>

                        <p>

                            No notifications available.

                        </p>

                    </div>

                )}

                {notifications
                    .slice(0, 5)
                    .map((notification) => (

                        <NotificationItem
                            key={
                                notification.notificationId
                            }
                            notification={notification}
                            onMarkAsRead={
                                onMarkAsRead
                            }
                            onDelete={onDelete}
                        />

                    ))}

            </div>

            {notifications.length > 0 && (

                <div className="notificationDropdownFooter">

                    <Link
                        to="/notifications"
                        onClick={onClose}
                    >

                        View All Notifications

                    </Link>

                </div>

            )}

        </div>

    );

}