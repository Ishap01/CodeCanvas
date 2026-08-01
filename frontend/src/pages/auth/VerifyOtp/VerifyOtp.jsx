import React, { useState } from "react";
import "./VerifyOtp.css";

import { Link, useLocation, useNavigate } from "react-router-dom";

import {
  FaCode,
  FaKey,
  FaCheckCircle,
  FaArrowLeft,
} from "react-icons/fa";

import bg from "../../../assets/images/login-bg.jpg";

import { verifyOtp } from "../../../services/authService";

export default function VerifyOtp() {

  const navigate = useNavigate();

  const location = useLocation();

  const email = location.state?.email || "";

  const [otp, setOtp] = useState("");

  const [message, setMessage] = useState("");

  const [success, setSuccess] = useState(false);

  const [loading, setLoading] = useState(false);

  const handleVerifyOtp = async (e) => {

    e.preventDefault();

    setMessage("");

    setSuccess(false);

    try {

      setLoading(true);

      await verifyOtp(email, otp);

      setSuccess(true);

      setMessage("OTP verified successfully.");

      setTimeout(() => {

        navigate("/reset-password", {
          state: {
            email,
          },
        });

      }, 1000);

    } catch (error) {

      setSuccess(false);

      setMessage(

        error.response?.data ||

        "Invalid OTP."

      );

    } finally {

      setLoading(false);

    }

  };

  return (

    <div className="verifyPage">

      <div
        className="verifyLeftPanel"
        style={{ backgroundImage: `url(${bg})` }}
      >

        <div className="verifyOverlay"></div>

        <div className="verifyLeftContent">

          <h1>

            Verify
            <br />
            <span>Your OTP</span>

          </h1>

          <p>

            Enter the OTP sent to your registered email.

          </p>

        </div>

      </div>

      <div className="verifyRightPanel">

        <form
          className="verifyCard"
          onSubmit={handleVerifyOtp}
        >

          <div className="verifyLogo">

            <FaCode />

          </div>

          <h2>Verify OTP</h2>

          <p className="verifySubtitle">

            We sent a 6-digit OTP to

            <br />

            <strong>{email}</strong>

          </p>

          <div className="verifyInputBox">

            <FaKey />

            <input
              type="text"
              placeholder="Enter OTP"
              value={otp}
              maxLength={6}
              onChange={(e) =>
                setOtp(e.target.value)
              }
              required
            />

          </div>

          {message && (

            <p
              className={
                success
                  ? "verifyMessage verifySuccess"
                  : "verifyMessage verifyError"
              }
            >

              {message}

            </p>

          )}

          <button
            type="submit"
            className="verifyButton"
            disabled={loading}
          >

            <FaCheckCircle />

            {loading
              ? "Verifying..."
              : "Verify OTP"}

          </button>

          <div className="verifyBack">

            <Link to="/forgot-password">

              <FaArrowLeft />

              Back

            </Link>

          </div>

        </form>

      </div>

    </div>

  );

}