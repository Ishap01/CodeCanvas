import React, { useState } from "react";
import "./Login.css";
import { Link, useNavigate } from "react-router-dom";

import {
  FaUser,
  FaLock,
  FaEye,
  FaEyeSlash,
  FaGithub,
  FaGoogle,
  FaCode,
  FaSignInAlt,
} from "react-icons/fa";

import bg from "../../assets/login-bg.jpg";

import { loginUser } from "../../services/authService";
import { useAuth } from "../../context/AuthContext";

export default function Login() {

  const navigate = useNavigate();

  const { login } = useAuth();

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [showPassword, setShowPassword] = useState(false);

  const [message, setMessage] = useState("");

  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {

    const { name, value } = event.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

  };

  const handleLogin = async (event) => {

    event.preventDefault();

    setMessage("");

    if (!formData.email.trim() || !formData.password.trim()) {
      setMessage("Email and Password are required.");
      return;
    }

    try {

      setLoading(true);

  const loginRequest = {
    email: formData.email.trim(),
    password: formData.password,
  };

const data = await loginUser(loginRequest);

      if (data.token) {

        login(data.token);

        navigate("/dashboard");

      } else {

        setMessage(data.message || "Login failed.");

      }

    } catch (error) {

      console.error(error);

      if (error.response) {

        setMessage(
          error.response.data.message || "Invalid credentials."
        );

      } else {

        setMessage("Unable to connect to the server.");

      }

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
            Welcome back to <br />
            <span>CodeCanvas.</span>
          </h1>

          <p>
            The visual code collaboration platform
            <br />
            for modern developers.
          </p>
        </div>
      </div>

      {/* RIGHT PANEL */}

      <div className="rightPanel">

        <form
          className="loginCard"
          onSubmit={handleLogin}
        >

          <div className="logo">
            <FaCode />
          </div>

          <h2>Welcome Back</h2>

          <p>Sign in to your CodeCanvas account</p>

          <div className="inputBox">

            <FaUser />

            <input
              autoFocus
              type="email"
              name="email"
              placeholder="Enter Email"
              value={formData.email}
              onChange={handleChange}
            />

          </div>

          <div className="inputBox">

            <FaLock />

            <input
              autoFocus
              type={showPassword ? "text" : "password"}
              name="password"
              placeholder="Password"
              value={formData.password}
              onChange={handleChange}
            />

            <span
              className="eye"
              onClick={() =>
                setShowPassword(!showPassword)
              }
            >
              {showPassword ? <FaEyeSlash /> : <FaEye />}
            </span>

          </div>

          <div className="options">

            <label>

              <input autoFocus type="checkbox" />

              Remember me

            </label>

            <Link to="/forgot-password">
              Forgot Password?
            </Link>

          </div>

          {message && (
            <p className="formMessage">
              {message}
            </p>
          )}

          <button
            type="submit"
            className="loginBtn"
            disabled={loading}
          >

            <FaSignInAlt />

            {loading
              ? "Signing In..."
              : "Sign In"}

          </button>

          <button
            type="button"
            className="socialBtn"
          >
            <FaGithub />
            Continue with GitHub
          </button>

          <button
            type="button"
            className="socialBtn"
          >
            <FaGoogle />
            Continue with Google
          </button>

          <div className="signup">

            Don't have an account?

            <Link to="/register">
              <span>Create one</span>
            </Link>

          </div>

        </form>

      </div>

    </div>

  );

}