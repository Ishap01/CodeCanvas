import React, { useState } from "react";
import "./ForgotPassword.css";

import { Link } from "react-router-dom";

import {
  FaUser,
  FaCode,
  FaPaperPlane,
  FaArrowLeft,
} from "react-icons/fa";

import bg from "../../assets/login-bg.jpg";

export default function ForgotPassword() {
  const [formData, setFormData] = useState({
    username: "",
  });

  const [message, setMessage] = useState("");
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleForgotPassword = async (event) => {
    event.preventDefault();

    setMessage("");
    setSuccess(false);

    if (!formData.username.trim()) {
      setMessage("Username is required");
      return;
    }

    try {
      setLoading(true);

      const response = await fetch(
        "http://localhost:8082/api/auth/forgot-password",
        {
          method: "POST",

          headers: {
            "Content-Type": "application/json",
          },

          body: JSON.stringify(formData),
        }
      );

      const data = await response.json();

      if (data.success) {
        setSuccess(true);
        setMessage(
          data.message ||
            "Password reset instructions have been sent successfully"
        );
      } else {
        setSuccess(false);
        setMessage(
          data.message || "Unable to process forgot password request"
        );
      }
    } catch (error) {
      console.error("Forgot password error:", error);

      setSuccess(false);
      setMessage("Backend server se connection nahi ho raha");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="forgotPage">
      {/* LEFT PANEL */}

      <div
        className="forgotLeftPanel"
        style={{ backgroundImage: `url(${bg})` }}
      >
        <div className="forgotOverlay"></div>

        <div className="forgotLeftContent">
          <h1>
            Recover your <br />
            <span>CodeCanvas.</span> account
          </h1>

          <p>
            Enter your username and follow the instructions
            <br />
            to recover access to your account.
          </p>
        </div>
      </div>

      {/* RIGHT PANEL */}

      <div className="forgotRightPanel">
        <form
          className="forgotCard"
          onSubmit={handleForgotPassword}
        >
          <div className="forgotLogo">
            <FaCode />
          </div>

          <h2>Forgot Password?</h2>

          <p className="forgotSubtitle">
            Enter your username to receive password reset instructions
          </p>

          <div className="forgotInputBox">
            <FaUser />

            <input
              type="text"
              name="username"
              placeholder="Username"
              value={formData.username}
              onChange={handleChange}
              autoComplete="username"
            />
          </div>

          {message && (
            <p
              className={
                success
                  ? "forgotMessage forgotSuccess"
                  : "forgotMessage forgotError"
              }
            >
              {message}
            </p>
          )}

          <button
            type="submit"
            className="forgotButton"
            disabled={loading}
          >
            <FaPaperPlane />

            {loading
              ? "Sending..."
              : "Send Reset Instructions"}
          </button>

          <div className="forgotBack">
            <Link to="/login">
              <FaArrowLeft />
              Back to Login
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
}