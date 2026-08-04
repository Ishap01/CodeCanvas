import "./ProfileHeader.css";

const ProfileHeader = ({ profile }) => {

    if (!profile) {
        return null;
    }

    return (

        <section className="profile-header">
<div className="profile-details">

    <div className="profile-avatar-wrapper">

        <div className="profile-avatar">

            {
                profile.profileImage ?

                    <img
                        src={profile.profileImage}
                        alt={profile.fullName}
                    />

                :

                    <div className="profile-avatar-placeholder">

                        {profile.fullName.charAt(0).toUpperCase()}

                    </div>

            }

        </div>

    </div>

    <h1>{profile.fullName}</h1>

    <p className="profile-username">

        @{profile.username}

    </p>

    <p className="profile-bio">

        {
            profile.bio ||
            "Developer on CodeCanvas."
        }

    </p>

    {
        profile.ownProfile ?

            <button
                className="profile-button"
                type="button"
            >
                Edit Profile
            </button>

        :

            <button
                className={
                    profile.following
                        ? "profile-button following"
                        : "profile-button"
                }
                type="button"
            >
                {
                    profile.following
                        ? "Following"
                        : "Follow"
                }
            </button>
    }

    <div className="profile-stats">

        <div className="profile-stat">

            <h3>{profile.followersCount}</h3>

            <span>Followers</span>

        </div>

        <div className="profile-stat">

            <h3>{profile.followingCount}</h3>

            <span>Following</span>

        </div>

    </div>

</div>

        </section>

    );

};

export default ProfileHeader;