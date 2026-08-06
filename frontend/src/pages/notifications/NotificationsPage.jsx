import { useEffect, useState } from "react";
import { getNotifications } from "../../services/notificationService";
import { FaBell } from "react-icons/fa";
import "./NotificationsPage.css";

export default function NotificationsPage() {

    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        loadNotifications();
    }, []);

    async function loadNotifications() {
        try {
            const data = await getNotifications();
            setNotifications(data);
        } catch (err) {
            console.error(err);
        }
    }

    return (
    <main className="notificationsPage">

        <div className="notificationsContainer">

            <h1 className="notificationsHeading">
                Notifications
            </h1>

            {notifications.length === 0 ? (

                <div className="emptyNotifications">

                    <h2>No Notifications</h2>

                    <p>
                        You're all caught up 🎉
                    </p>

                </div>

            ) : (

                <div className="notificationsList">

                    {notifications.map(notification => (

                        <div
                            key={notification.notificationId}
                            className="notificationCard"
                        >

                            <div className="notificationIcon">
                                <FaBell />
                            </div>

                            <div className="notificationContent">

                                <h3 className="notificationTitle">
                                    {notification.title}
                                </h3>

                                <p className="notificationMessage">
                                    {notification.message}
                                </p>

                                <span className="notificationTime">
                                    {new Date(notification.createdAt).toLocaleString()}
                                </span>

                            </div>

                        </div>

                    ))}

                </div>

            )}

        </div>

    </main>
);
}