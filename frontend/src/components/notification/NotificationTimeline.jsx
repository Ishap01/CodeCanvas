import "./NotificationTimeline.css";

import useNotifications from "../../hooks/useNotifications";
import NotificationItem from "./NotificationItem";

export default function NotificationTimeline() {

    const {

        notifications,
        loading,
        error,
        markNotificationAsRead,
        removeNotification,

    } = useNotifications();

    if (loading) {

        return (

            <div className="notificationTimelineLoading">

                Loading activity...

            </div>

        );

    }

    if (error) {

        return (

            <div className="notificationTimelineError">

                {error}

            </div>

        );

    }

    if (notifications.length === 0) {

        return (

            <div className="notificationTimelineEmpty">

                <h2>No Activity Yet</h2>

                <p>

                    Your likes, comments, follows,
                    bookmarks and other notifications
                    will appear here.

                </p>

            </div>

        );

    }

    const groupedNotifications =
        groupNotifications(notifications);

    return (

        <div className="notificationTimeline">

            {Object.entries(groupedNotifications)
                .map(([section, items]) => (

                    <div
                        key={section}
                        className="notificationSection"
                    >

                        <h3>

                            {section}

                        </h3>

                        {items.map(notification => (

                            <NotificationItem
                                key={
                                    notification.notificationId
                                }
                                notification={
                                    notification
                                }
                                onMarkAsRead={
                                    markNotificationAsRead
                                }
                                onDelete={
                                    removeNotification
                                }
                            />

                        ))}

                    </div>

                ))}

        </div>

    );

}

function groupNotifications(
    notifications
) {

    const groups = {

        Today: [],
        Yesterday: [],
        Earlier: [],

    };

    const today =
        new Date();

    today.setHours(
        0,
        0,
        0,
        0
    );

    const yesterday =
        new Date(today);

    yesterday.setDate(
        yesterday.getDate() - 1
    );

    notifications.forEach(notification => {

        const createdAt =
            new Date(
                notification.createdAt
            );

        const date =
            new Date(createdAt);

        date.setHours(
            0,
            0,
            0,
            0
        );

        if (
            date.getTime() ===
            today.getTime()
        ) {

            groups.Today.push(
                notification
            );

        }

        else if (

            date.getTime() ===
            yesterday.getTime()

        ) {

            groups.Yesterday.push(
                notification
            );

        }

        else {

            groups.Earlier.push(
                notification
            );

        }

    });

    return Object.fromEntries(

        Object.entries(groups)
            .filter(
                ([, value]) =>
                    value.length > 0
            )

    );

}