import { useState } from "react";
import { useNavigate } from "react-router-dom";

import RazorpayCheckout from "../../components/payment/RazorpayCheckout";

import "./PricingPage.css";

function PricingPage() {
  const navigate = useNavigate();

  const [successMessage, setSuccessMessage] = useState("");
  const [failureMessage, setFailureMessage] = useState("");

  const premiumPlanId = 2;

  const userDetails = {
    name: localStorage.getItem("fullName") || "",
    email: localStorage.getItem("email") || "",
    contact: localStorage.getItem("mobileNumber") || "",
  };

  const handleFreePlan = () => {
    setFailureMessage("");
    setSuccessMessage(
      "Free plan does not require payment. You can continue using CodeCanvas."
    );

    setTimeout(() => {
      navigate("/dashboard");
    }, 1200);
  };

  const handlePaymentSuccess = (response) => {
    setFailureMessage("");

    setSuccessMessage(
      response?.message ||
        "Payment verified successfully. Premium subscription activated."
    );

    setTimeout(() => {
      navigate("/dashboard");
    }, 1800);
  };

  const handlePaymentFailure = (message) => {
    setSuccessMessage("");
    setFailureMessage(
      message || "Payment could not be completed."
    );
  };

  return (
    <main className="pricing-page">
      <section className="pricing-header">
        <p className="pricing-eyebrow">CodeCanvas Membership</p>

        <h1>Choose the plan that fits you</h1>

        <p className="pricing-subtitle">
          Start with the free plan or unlock premium features with
          a monthly subscription.
        </p>
      </section>

      {successMessage && (
        <div className="pricing-alert pricing-alert-success">
          {successMessage}
        </div>
      )}

      {failureMessage && (
        <div className="pricing-alert pricing-alert-error">
          {failureMessage}
        </div>
      )}

      <section className="pricing-card-container">
        <article className="pricing-card">
          <div className="pricing-card-header">
            <span className="pricing-plan-label">Free</span>

            <h2>Free Tier</h2>

            <div className="pricing-price">
              <span className="pricing-currency">₹</span>
              <span className="pricing-amount">0</span>
              <span className="pricing-duration">/ forever</span>
            </div>
          </div>

          <ul className="pricing-feature-list">
            <li>Browse public code snippets</li>
            <li>Create and manage basic snippets</li>
            <li>Like, comment and bookmark snippets</li>
            <li>Basic search access</li>
          </ul>

          <button
            type="button"
            className="pricing-secondary-button"
            onClick={handleFreePlan}
          >
            Continue Free
          </button>

          <p className="pricing-plan-note">
            No payment or Razorpay checkout required.
          </p>
        </article>

        <article className="pricing-card pricing-card-premium">
          <div className="pricing-popular-badge">
            Recommended
          </div>

          <div className="pricing-card-header">
            <span className="pricing-plan-label">
              Premium
            </span>

            <h2>Premium Monthly</h2>

            <div className="pricing-price">
              <span className="pricing-currency">₹</span>
              <span className="pricing-amount">299</span>
              <span className="pricing-duration">/ month</span>
            </div>
          </div>

          <ul className="pricing-feature-list">
            <li>Everything included in Free Tier</li>
            <li>Access premium code snippets</li>
            <li>AI-powered code explanations</li>
            <li>Automatic tag generation</li>
            <li>Advanced search features</li>
            <li>Premium member badge</li>
          </ul>

          <RazorpayCheckout
            planId={premiumPlanId}
            buttonText="Buy Premium"
            userDetails={userDetails}
            onPaymentSuccess={handlePaymentSuccess}
            onPaymentFailure={handlePaymentFailure}
          />

          <p className="pricing-plan-note">
            Secure test payment powered by Razorpay.
          </p>
        </article>
      </section>
    </main>
  );
}

export default PricingPage;