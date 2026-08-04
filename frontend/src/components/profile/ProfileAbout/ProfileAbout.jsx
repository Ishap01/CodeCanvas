import "./ProfileAbout.css";

const ProfileAbout = ({ bio }) => {

    return (

        <section className="profileAboutCard">

            <span className="profileAboutBadge">
                ABOUT
            </span>

            <p className="profileAboutText">

                {
                    bio && bio.trim().length > 0
                        ? bio
                        : "No bio added yet."
                }

            </p>

        </section>

    );

};

export default ProfileAbout;