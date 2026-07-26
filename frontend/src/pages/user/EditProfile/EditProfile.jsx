import React, { useEffect, useState } from "react";
import "./EditProfile.css";

import { FaUser, FaCamera } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

import {
    getProfile,
    updateProfile,
    uploadProfileImage,
} from "../../../services/userService";

export default function EditProfile() {

    const navigate = useNavigate();

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);

    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    const [selectedImage, setSelectedImage] =
        useState(null);

    const [previewImage, setPreviewImage] =
        useState("");

    const [formData, setFormData] = useState({
        fullName: "",
        username: "",
        mobileNumber: "",
        bio: "",
    });

    useEffect(() => {

        loadProfile();

    }, []);

    const loadProfile = async () => {

        try {

            setLoading(true);

            const profile = await getProfile();

            setFormData({

                fullName: profile.fullName || "",

                username: profile.username || "",

                mobileNumber:
                    profile.mobileNumber || "",

                bio: profile.bio || "",

            });

            setPreviewImage(profile.profileImage || "");

        } catch (err) {

            setError(

                err.response?.data?.message ||

                "Unable to load profile."

            );

        } finally {

            setLoading(false);

        }

    };

    const handleInputChange = (e) => {

        setFormData({

            ...formData,

            [e.target.name]: e.target.value,

        });

    };

    const handleImageChange = (e) => {

        const file = e.target.files[0];

        if (!file) return;

        setSelectedImage(file);

        setPreviewImage(

            URL.createObjectURL(file)

        );

    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            setSaving(true);

            setMessage("");

            setError("");

            if (selectedImage) {

                await uploadProfileImage(

                    selectedImage

                );

            }

            await updateProfile(formData);

            setMessage(

                "Profile updated successfully."

            );

            setTimeout(() => {

                navigate("/profile");

            }, 1200);

        } catch (err) {

            setError(

                err.response?.data?.message ||

                "Unable to update profile."

            );

        } finally {

            setSaving(false);

        }

    };

    if (loading) {

    return (

        <div className="editProfilePage">

            <div className="editProfileLoading">

                Loading Profile...

            </div>

        </div>

    );

}

    return (

        <div className="editProfilePage">

            <div className="editProfileContainer">

                <h1>Edit Profile</h1>

                <form onSubmit={handleSubmit}>

                    <div className="editProfileImageSection">

                        <div className="editProfileAvatar">

                            {previewImage ? (

                                <img

                                    src={previewImage}

                                    alt="Profile"

                                />

                            ) : (

                                <FaUser />

                            )}

                        </div>

                        <label className="imageUploadButton">

                            <FaCamera />

                            Change Photo

                            <input

                                type="file"

                                accept="image/*"

                                onChange={handleImageChange}

                                hidden

                            />

                        </label>

                    </div>

                    <div className="editProfileFormGroup">

                        <label>Full Name</label>

                        <input
                            type="text"
                            name="fullName"
                            value={formData.fullName}
                            onChange={handleInputChange}
                            placeholder="Enter full name"
                        />

                    </div>

                    <div className="editProfileFormGroup">

                        <label>Username</label>

                        <input
                            type="text"
                            name="username"
                            value={formData.username}
                            onChange={handleInputChange}
                            placeholder="Enter username"
                        />

                    </div>

                    <div className="editProfileFormGroup">

                        <label>Mobile Number</label>

                        <input
                            type="text"
                            name="mobileNumber"
                            value={formData.mobileNumber}
                            onChange={handleInputChange}
                            placeholder="Enter mobile number"
                        />

                    </div>

                    <div className="editProfileFormGroup">

                        <label>Bio</label>

                        <textarea
                            name="bio"
                            rows="5"
                            value={formData.bio}
                            onChange={handleInputChange}
                            placeholder="Tell everyone about yourself..."
                        />

                    </div>

                    {message && (

                        <div className="successMessage">

                            {message}

                        </div>

                    )}

                    {error && (

                        <div className="errorMessage">

                            {error}

                        </div>

                    )}

                    <div className="editProfileButtons">

                        <button
                            type="button"
                            className="cancelButton"
                            onClick={() => navigate("/profile")}
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            className="saveButton"
                            disabled={saving}
                        >
                            {saving
                                ? "Saving..."
                                : "Save Changes"}
                        </button>

                    </div>

                </form>

            </div>

        </div>

    );

}