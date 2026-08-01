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

import bg from "../../../assets/images/login-bg.jpg";

import { registerUser } from "../../../services/authService";

function Register() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    username: "",
    mobileNumber: "",
    password: "",
    confirmPassword: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleRegister = async (event) => {
    event.preventDefault();

    setMessage("");

    if (
      !formData.fullName.trim() ||
      !formData.email.trim() ||
      !formData.username.trim() ||
      !formData.mobileNumber.trim() ||
      !formData.password.trim() ||
      !formData.confirmPassword.trim()
    ) {
      setMessage("All fields are required.");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailRegex.test(formData.email)) {
      setMessage("Please enter a valid email address.");
      return;
    }

    if (/\s/.test(formData.username)) {
    setMessage("Username cannot contain spaces.");
    return;
    }

    if (!/^\d{10}$/.test(formData.mobileNumber)) {
      setMessage("Enter a valid 10-digit mobile number.");
      return;
    }

    if (formData.password.length < 8) {
      setMessage("Password must be at least 8 characters.");
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setMessage("Password and Confirm Password do not match.");
      return;
    }

    try {
      setLoading(true);

      const data = await registerUser(formData);

      setMessage(data.message);

      if (data.success) {
        setTimeout(() => {
          navigate("/login");
        }, 1500);
      }
    } catch (error) {
      console.error(error);

      if (error.response) {
        setMessage(error.response.data.message);
      } else {
        setMessage("Unable to connect to the server.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="loginPage">
      {/* Left Panel */}

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

      {/* Right Panel */}

      <div className="rightPanel">
        <form className="loginCard" onSubmit={handleRegister}>
          <div className="logo">
            <FaCode />
          </div>

          <h2>Create Account</h2>

          <p>Create your CodeCanvas account</p>

          {/* Full Name */}

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

          {/* Email */}

          <div className="inputBox">
            <FaUser />

            <input
              type="email"
              name="email"
              placeholder="Email"
              value={formData.email}
              onChange={handleChange}
            />
          </div>

          {/* Username */}

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

          {/* Mobile */}

          <div className="inputBox">
            <FaPhone />

            <input
              type="tel"
              name="mobileNumber"
              placeholder="Mobile Number"
              maxLength={10}
              value={formData.mobileNumber}
              onChange={handleChange}
            />
          </div>

          {/* Password */}

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

          {/* Confirm Password */}

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
              <span> Login</span>
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
}

export default Register;