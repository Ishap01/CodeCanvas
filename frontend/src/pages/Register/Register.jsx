import React, { useState } from "react";
import "./Register.css";

import { Link, useNavigate } from "react-router-dom";

import {
  FaUser,
  FaLock,
  FaPhone,
  FaCode,
  FaArrowRight,
  FaEye,
  FaEyeSlash,
} from "react-icons/fa";

import bg from "../../assets/login-bg.jpg";

function Register() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    fullName: "",
    mobileNumber: "",
    username: "",
    password: "",
    confirmPassword: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] =
    useState(false);

  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  // Har input ki value formData me update karega
  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData({
      ...formData,
      [name]: value,
    });
  };

  // Register API call
  const handleRegister = async (event) => {
    event.preventDefault();

    setMessage("");

    if (
      !formData.fullName.trim() ||
      !formData.mobileNumber.trim() ||
      !formData.username.trim() ||
      !formData.password.trim() ||
      !formData.confirmPassword.trim()
    ) {
      setMessage("All fields are required");
      return;
    }

    if (formData.mobileNumber.length !== 10) {
      setMessage("Mobile number must contain 10 digits");
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setMessage("Password and confirm password do not match");
      return;
    }

    try {
      setLoading(true);

      const response = await fetch(
        "http://localhost:8082/api/auth/register",
        {
          method: "POST",

          headers: {
            "Content-Type": "application/json",
          },

          body: JSON.stringify(formData),
        }
      );

      const data = await response.json();

      setMessage(data.message);

      if (data.success) {
        // Registration success ke baad login page
        setTimeout(() => {
          navigate("/login");
        }, 1000);
      }
    } catch (error) {
      console.error("Registration error:", error);
      setMessage("Backend server se connection nahi ho raha");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="loginPage">
      {/* LEFT PANEL */}

      <div
        className="leftPanel"
        style={{ backgroundImage: `url(${bg})` }}
      >
        <div className="overlay"></div>

        <div className="leftContent">
          <h1>
            Join
            <br />
            <span>CodeCanvas.</span>
          </h1>

          <p>Create your account and start collaborating.</p>
        </div>
      </div>

      {/* RIGHT PANEL */}

      <div className="rightPanel">
        <form className="loginCard" onSubmit={handleRegister}>
          <div className="logo">
            <FaCode />
          </div>

          <h2>Create Account</h2>

          <p>Create your CodeCanvas account</p>

          {/* FULL NAME */}

          <div className="inputBox">
            <FaUser />

            <input
              type="text"
              name="fullName"
              placeholder="Full Name"
              value={formData.fullName}
              onChange={handleChange}
            />
          </div>

          {/* USERNAME */}

          <div className="inputBox">
            <FaUser />

            <input
              type="text"
              name="username"
              placeholder="Username"
              value={formData.username}
              onChange={handleChange}
            />
          </div>

          {/* MOBILE NUMBER */}

          <div className="inputBox">
            <FaPhone />

            <input
              type="tel"
              name="mobileNumber"
              placeholder="Mobile Number"
              maxLength="10"
              value={formData.mobileNumber}
              onChange={handleChange}
            />
          </div>

          {/* PASSWORD */}

          <div className="inputBox">
            <FaLock />

            <input
              type={showPassword ? "text" : "password"}
              name="password"
              placeholder="Password"
              value={formData.password}
              onChange={handleChange}
            />

            <span
              className="eye"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
          </div>

          {/* CONFIRM PASSWORD */}

          <div className="inputBox">
            <FaLock />

            <input
              type={showConfirmPassword ? "text" : "password"}
              name="confirmPassword"
              placeholder="Confirm Password"
              value={formData.confirmPassword}
              onChange={handleChange}
            />

            <span
              className="eye"
              onClick={() =>
                setShowConfirmPassword(!showConfirmPassword)
              }
            >
              {showConfirmPassword ? <FaEyeSlash /> : <FaEye />}
            </span>
          </div>

          {message && <p className="formMessage">{message}</p>}

          <button
            type="submit"
            className="loginBtn"
            disabled={loading}
          >
            <FaArrowRight />

            {loading ? "Creating Account..." : "Create Account"}
          </button>

          <div className="signup">
            Already have an account?

            <Link to="/login">
              <span>Login</span>
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
}

export default Register;