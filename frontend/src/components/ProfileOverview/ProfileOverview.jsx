import "./ProfileOverview.css";

const ProfileOverview = ({ profile }) => {

    if (!profile) {
        return null;
    }

    return (

        <div className="profile-overview">

            <div className="overview-card">

                <h2>About</h2>

                <p>

                    {
                        profile.bio
                            ? profile.bio
                            : "This developer hasn't added a bio yet."
                    }

                </p>

            </div>

            <div className="overview-card">

                <h2>Developer Information</h2>

                <div className="overview-grid">

                    <div className="overview-item">

                        <span>Username</span>

                        <strong>

                            @{profile.username}

                        </strong>

                    </div>

                    <div className="overview-item">

                        <span>Followers</span>

                        <strong>

                            {profile.followersCount}

                        </strong>

                    </div>

                    <div className="overview-item">

                        <span>Following</span>

                        <strong>

                            {profile.followingCount}

                        </strong>

                    </div>

                    <div className="overview-item">

                        <span>Member Since</span>

                        <strong>

                            {
                                profile.createdAt
                                    ? new Date(profile.createdAt).toLocaleDateString()
                                    : "-"
                            }

                        </strong>

                    </div>

                </div>

            </div>

        </div>

    );

};

export default ProfileOverview;