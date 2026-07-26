import "./Settings.css";

import {
    FaUserEdit,
    FaLock,
    FaBell,
    FaMoon,
    FaSignOutAlt,
    FaChevronRight,
} from "react-icons/fa";

import { useNavigate } from "react-router-dom";
import { logout } from "../../services/authService";

export default function Settings() {

    const navigate = useNavigate();

    const handleLogout = () => {

        logout();

        navigate("/login", { replace: true });

    };

    return (

        <div className="settingsPage">

            <div className="settingsContainer">

                <h1>Settings</h1>

                <p className="settingsSubtitle">
                    Manage your account preferences.
                </p>

                <div className="settingsSection">

                    <h3>Account</h3>

                    <div
                        className="settingsItem"
                        onClick={() => navigate("/edit-profile")}
                    >
                        <div className="settingsLeft">
                            <FaUserEdit />
                            <div>
                                <h4>Edit Profile</h4>
                                <p>Update your personal information</p>
                            </div>
                        </div>

                        <FaChevronRight />
                    </div>

                    <div
                        className="settingsItem"
                        onClick={() => navigate("/change-password")}
                    >
                        <div className="settingsLeft">
                            <FaLock />
                            <div>
                                <h4>Change Password</h4>
                                <p>Update your account password</p>
                            </div>
                        </div>

                        <FaChevronRight />
                    </div>

                </div>

                <div className="settingsSection">

                    <h3>Preferences</h3>

                    <div className="settingsItem">

                        <div className="settingsLeft">
                            <FaBell />
                            <div>
                                <h4>Notifications</h4>
                                <p>Coming Soon</p>
                            </div>
                        </div>

                        <FaChevronRight />

                    </div>

                    <div className="settingsItem">

                        <div className="settingsLeft">
                            <FaMoon />
                            <div>
                                <h4>Appearance</h4>
                                <p>Dark Theme</p>
                            </div>
                        </div>

                        <FaChevronRight />

                    </div>

                </div>

                <div className="settingsSection">

                    <h3>Account</h3>

                    <div
                        className="settingsItem logout"
                        onClick={handleLogout}
                    >
                        <div className="settingsLeft">
                            <FaSignOutAlt />
                            <div>
                                <h4>Logout</h4>
                                <p>Sign out of your account</p>
                            </div>
                        </div>

                        <FaChevronRight />
                    </div>
                </div>

            </div>

        </div>

    );

}