export const groupNotifications = (
    notifications
) => {

    const today = new Date();

    const yesterday = new Date();

    yesterday.setDate(
        yesterday.getDate() - 1
    );

    const isSameDate = (
        firstDate,
        secondDate
    ) => {

        return (

            firstDate.getFullYear() ===
            secondDate.getFullYear() &&

            firstDate.getMonth() ===
            secondDate.getMonth() &&

            firstDate.getDate() ===
            secondDate.getDate()

        );

    };

    return {

        today: notifications.filter(
            (notification) =>

                isSameDate(
                    new Date(
                        notification.createdAt
                    ),
                    today
                )
        ),

        yesterday:
            notifications.filter(
                (notification) =>

                    isSameDate(
                        new Date(
                            notification.createdAt
                        ),
                        yesterday
                    )
            ),

        earlier:
            notifications.filter(
                (notification) => {

                    const date =
                        new Date(
                            notification.createdAt
                        );

                    return (

                        !isSameDate(
                            date,
                            today
                        ) &&

                        !isSameDate(
                            date,
                            yesterday
                        )

                    );

                }
            ),

    };

};

export const formatNotificationTime = (
    createdAt
) => {

    const now = new Date();

    const created =
        new Date(createdAt);

    const difference =
        now - created;

    const minutes =
        Math.floor(
            difference / 60000
        );

    if (minutes < 1)
        return "Just now";

    if (minutes < 60)
        return `${minutes} min ago`;

    const hours =
        Math.floor(minutes / 60);

    if (hours < 24)
        return `${hours} hour${hours > 1 ? "s" : ""} ago`;

    const days =
        Math.floor(hours / 24);

    if (days < 7)
        return `${days} day${days > 1 ? "s" : ""} ago`;

    return created.toLocaleDateString();
};