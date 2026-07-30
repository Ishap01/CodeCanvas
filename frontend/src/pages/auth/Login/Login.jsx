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

export default function Login() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  // Input me jo value type hogi, wo formData me store hogi
  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData({
      ...formData,
      [name]: value,
    });
  };

  // Login button click hone par
  const handleLogin = async (event) => {
    event.preventDefault();

    setMessage("");

    if (!formData.username.trim() || !formData.password.trim()) {
      setMessage("Username and password are required");
      return;
    }

    try {
      setLoading(true);

      const response = await fetch(
          "http://localhost:8082/api/auth/login",
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
        setMessage(data.message);

        // JWT implement hone ke baad token save hoga
        if (data.token) {
          localStorage.setItem("token", data.token);
        }

        // Login success ke baad dashboard
        navigate("/dashboard");
      } else {
        setMessage(data.message || "Invalid username or password");
      }
    } catch (error) {
      console.error("Login error:", error);
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
          <form className="loginCard" onSubmit={handleLogin}>
            <div className="logo">
              <FaCode />
            </div>

            <h2>Welcome Back</h2>

            <p>Sign in to your CodeCanvas account</p>

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

            <div className="options">
              <label>
                <input type="checkbox" />
                Remember me
              </label>

              <Link to="/forgot-password">Forgot password?</Link>
            </div>

            {message && <p className="formMessage">{message}</p>}

            <button
                type="submit"
                className="loginBtn"
                disabled={loading}
            >
              <FaSignInAlt />

              {loading ? "Signing In..." : "Sign In"}
            </button>

            <button type="button" className="socialBtn">
              <FaGithub />
              Continue with GitHub
            </button>

            <button type="button" className="socialBtn">
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