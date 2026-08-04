import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import { getPublicProfile } from "../../../services/userService";
import ProfileHeader from "../../../components/ProfileHeader/ProfileHeader";
import ProfileTabs from "../../../components/ProfileTabs/ProfileTabs";
import "./PublicProfile.css";
import ProfileAbout from "../../../components/profile/ProfileAbout/ProfileAbout";


const PublicProfile = () => {

    const { username } = useParams();

    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [activeTab, setActiveTab] = useState("Overview");

    useEffect(() => {
        fetchProfile();
    }, [username]);

    const fetchProfile = async () => {
        try {
            setLoading(true);

            const data = await getPublicProfile(username);

            setProfile(data);
        } catch (err) {
            console.error(err);
            setError("Unable to load profile.");
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="public-profile-page">
                <div className="profile-content-area">
                    <div className="publicProfileLoadingState">
                        <span className="publicProfileSpinner" />
                        <p>Loading profile...</p>
                    </div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="public-profile-page">
                <div className="profile-content-area">
                    <div className="publicProfileErrorState">
                        <span className="publicProfileStateIcon">!</span>
                        <h2>Profile unavailable</h2>
                        <p>{error}</p>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="public-profile-page">

            <ProfileHeader profile={profile} />

            <ProfileTabs
                activeTab={activeTab}
                setActiveTab={setActiveTab}
            />

            <div className="profile-content-area">



                {
                    activeTab === "Overview" &&
                    <div className="publicProfilePanel">
                        <h2 className="publicProfilePanelTitle">Overview</h2>
                        <p className="publicProfilePanelText">
                            Profile overview content will appear here.
                        </p>
                    </div>
                }

                {
                    activeTab === "Snippets" &&
                    <div className="publicProfileEmptyState">
                        <span className="publicProfileStateIcon">&lt;/&gt;</span>
                        <h2>Snippets</h2>
                        <p>
                            Public snippets for this profile will appear here.
                        </p>
                    </div>
                }


            </div>

            {
                activeTab === "About" &&

                <ProfileAbout
                    bio={profile.bio}
                />

            }

        </div>
    );
};

export default PublicProfile;
