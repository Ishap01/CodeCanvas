import "./Notifications.css";

import NotificationTimeline
from "../../components/notification/NotificationTimeline";

export default function Notifications() {

    return (

        <div className="notificationsPage">

            <div className="notificationsHeader">

                <div>

                    <h1>Notifications</h1>

                    <p>
                        Stay updated with likes,
                        comments, replies, follows,
                        bookmarks, forks and system
                        updates.
                    </p>

                </div>

            </div>

            <div className="notificationsCard">

                <NotificationTimeline />

            </div>

        </div>

    );

}