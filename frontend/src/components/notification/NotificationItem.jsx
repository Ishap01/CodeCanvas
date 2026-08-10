import "./NotificationItem.css";

import {
    FaBell,
    FaBookmark,
    FaCodeBranch,
    FaCommentDots,
    FaCrown,
    FaHeart,
    FaReply,
    FaUserPlus,
} from "react-icons/fa";

const notificationIcons = {

    LIKE: <FaHeart />,

    COMMENT: <FaCommentDots />,

    REPLY: <FaReply />,

    FOLLOW: <FaUserPlus />,

    BOOKMARK: <FaBookmark />,

    FORK: <FaCodeBranch />,

    SNIPPET_CREATED: <FaCodeBranch />,

    PREMIUM: <FaCrown />,

    SYSTEM: <FaBell />,

};

export default function NotificationItem({

    notification,

    onMarkAsRead,

    onDelete,

}) {

    const {

        notificationId,

        notificationType,

        title,

        message,

        isRead,

        createdAt,

    } = notification;

    const handleClick = () => {

        if (!isRead && onMarkAsRead) {

            onMarkAsRead(notificationId);

        }

    };

    return (

        <div
            className={`notificationItem ${isRead
                    ? "notificationRead"
                    : "notificationUnread"
                }`}
            onClick={handleClick}
        >

            <div className="notificationIcon">

                {notificationIcons[
                    notificationType
                ] || <FaBell />}

            </div>

            <div className="notificationContent">

                <h4>
                    {title}
                </h4>

                <p>
                    {message}
                </p>

                <span>
                    {formatTime(createdAt)}
                </span>

            </div>

            {onDelete && (

                <button
                    className="notificationDelete"
                    onClick={(event) => {

                        event.stopPropagation();

                        onDelete(notificationId);

                    }}
                >

                    ×

                </button>

            )}

        </div>

    );

}

function formatTime(date) {

    if (!date) {

        return "";

    }

    const notificationDate =
        new Date(date);

    const seconds =
        Math.floor(
            (Date.now() -
                notificationDate.getTime()) /
            1000
        );

    if (seconds < 60) {

        return "Just now";

    }

    const minutes =
        Math.floor(seconds / 60);

    if (minutes < 60) {

        return `${minutes} min ago`;

    }

    const hours =
        Math.floor(minutes / 60);

    if (hours < 24) {

        return `${hours} hr ago`;

    }

    const days =
        Math.floor(hours / 24);

    if (days === 1) {

        return "Yesterday";

    }

    if (days < 7) {

        return `${days} days ago`;

    }

    return notificationDate
        .toLocaleDateString();
}