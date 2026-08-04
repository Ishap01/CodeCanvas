import "./ProfileTabs.css";

const ProfileTabs = ({ activeTab, setActiveTab }) => {

    const tabs = [
    "Overview",
    "Snippets",
    "About"
];

    return (

        <section className="profile-tabs">

            <div className="profile-tabs-container">

                {
                    tabs.map((tab) => (

                        <button
                            key={tab}
                            className={
                                activeTab === tab
                                    ? "profile-tab active"
                                    : "profile-tab"
                            }
                            onClick={() => setActiveTab(tab)}
                        >
                            {tab}
                        </button>

                    ))
                }

            </div>

        </section>

    );

};

export default ProfileTabs;