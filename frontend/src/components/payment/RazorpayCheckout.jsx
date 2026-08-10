// import { useState } from "react";

// import {
//   createPaymentOrder,
//   verifyPayment,
// } from "../../services/paymentService";

// import { loadRazorpayScript } from "../../utils/razorpay";

// function RazorpayCheckout({
//   planId,
//   buttonText = "Buy Premium",
//   disabled = false,
//   userDetails = {},
//   onPaymentSuccess,
//   onPaymentFailure,
// }) {
//   const [loading, setLoading] = useState(false);
//   const [errorMessage, setErrorMessage] = useState("");

//   const handlePayment = async () => {
//     if (!planId) {
//       setErrorMessage("Subscription plan ID is required.");
//       return;
//     }

//     setLoading(true);
//     setErrorMessage("");

//     try {
//       const razorpayLoaded = await loadRazorpayScript();

//       if (!razorpayLoaded) {
//         throw new Error(
//           "Unable to load Razorpay Checkout. Please check your internet connection."
//         );
//       }

//       const orderResponse = await createPaymentOrder(planId);

//       if (!orderResponse?.success || !orderResponse?.data) {
//         throw new Error(
//           orderResponse?.message ||
//             "Unable to create Razorpay order."
//         );
//       }

//       const orderData = orderResponse.data;

//       const options = {
//         key: orderData.razorpayKey,
//         amount: orderData.amount,
//         currency: orderData.currency,
//         name: "CodeCanvas",
//         description: "Monthly Premium Subscription",
//         order_id: orderData.razorpayOrderId,

//         handler: async function (razorpayResponse) {
//           try {
//             const verificationResponse = await verifyPayment({
//               razorpayOrderId:
//                 razorpayResponse.razorpay_order_id,
//               razorpayPaymentId:
//                 razorpayResponse.razorpay_payment_id,
//               razorpaySignature:
//                 razorpayResponse.razorpay_signature,
//             });

//             if (!verificationResponse?.success) {
//               throw new Error(
//                 verificationResponse?.message ||
//                   "Payment verification failed."
//               );
//             }

//             if (onPaymentSuccess) {
//               onPaymentSuccess(verificationResponse);
//             }
//           } catch (verificationError) {
//             const message =
//               verificationError.message ||
//               "Payment verification failed.";

//             setErrorMessage(message);

//             if (onPaymentFailure) {
//               onPaymentFailure(message);
//             }
//           } finally {
//             setLoading(false);
//           }
//         },

//         prefill: {
//           name: userDetails.name || "",
//           email: userDetails.email || "",
//           contact: userDetails.contact || "",
//         },

//         notes: {
//           internalPaymentId: orderData.paymentId,
//           subscriptionPlanId: String(planId),
//           receipt: orderData.receipt,
//         },

//         theme: {
//           color: "#6c5ce7",
//         },

//         modal: {
//           ondismiss: function () {
//             setLoading(false);

//             const message =
//               "Payment window was closed before completion.";

//             setErrorMessage(message);

//             if (onPaymentFailure) {
//               onPaymentFailure(message);
//             }
//           },
//         },
//       };

//       const razorpayCheckout = new window.Razorpay(options);

//       razorpayCheckout.on(
//         "payment.failed",
//         function (failureResponse) {
//           const message =
//             failureResponse?.error?.description ||
//             "Razorpay payment failed.";

//           setErrorMessage(message);
//           setLoading(false);

//           if (onPaymentFailure) {
//             onPaymentFailure(message);
//           }
//         }
//       );

//       razorpayCheckout.open();
//     } catch (error) {
//       const message =
//         error.message || "Unable to initiate payment.";

//       setErrorMessage(message);
//       setLoading(false);

//       if (onPaymentFailure) {
//         onPaymentFailure(message);
//       }
//     }
//   };

//   return (
//     <div className="razorpay-checkout">
//       <button
//         type="button"
//         className="razorpay-checkout-button"
//         onClick={handlePayment}
//         disabled={disabled || loading}
//       >
//         {loading ? "Processing..." : buttonText}
//       </button>

//       {errorMessage && (
//         <p className="razorpay-checkout-error">
//           {errorMessage}
//         </p>
//       )}
//     </div>
//   );
// }

// export default RazorpayCheckout;



import { useState } from "react";

import {
  createPaymentOrder,
  markPaymentFailed,
  verifyPayment,
} from "../../services/paymentService";

import { loadRazorpayScript } from "../../utils/razorpay";

function RazorpayCheckout({
  planId,
  buttonText = "Buy Premium",
  disabled = false,
  userDetails = {},
  onPaymentSuccess,
  onPaymentFailure,
}) {
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handlePayment = async () => {
    if (!planId) {
      setErrorMessage("Subscription plan ID is required.");
      return;
    }

    setLoading(true);
    setErrorMessage("");

    try {
      const razorpayLoaded = await loadRazorpayScript();

      if (!razorpayLoaded) {
        throw new Error(
          "Unable to load Razorpay Checkout. Please check your internet connection."
        );
      }

      const orderResponse = await createPaymentOrder(planId);

      if (!orderResponse?.success || !orderResponse?.data) {
        throw new Error(
          orderResponse?.message ||
            "Unable to create Razorpay order."
        );
      }

      const orderData = orderResponse.data;

      const options = {
        key: orderData.razorpayKey,
        amount: orderData.amount,
        currency: orderData.currency,
        name: "CodeCanvas",
        description: "Monthly Premium Subscription",
        order_id: orderData.razorpayOrderId,

        handler: async function (razorpayResponse) {
          try {
            const verificationResponse = await verifyPayment({
              razorpayOrderId:
                razorpayResponse.razorpay_order_id,
              razorpayPaymentId:
                razorpayResponse.razorpay_payment_id,
              razorpaySignature:
                razorpayResponse.razorpay_signature,
            });

            if (!verificationResponse?.success) {
              throw new Error(
                verificationResponse?.message ||
                  "Payment verification failed."
              );
            }

            if (onPaymentSuccess) {
              onPaymentSuccess(verificationResponse);
            }
          } catch (verificationError) {
            const message =
              verificationError.message ||
              "Payment verification failed.";

            setErrorMessage(message);

            if (onPaymentFailure) {
              onPaymentFailure(message);
            }
          } finally {
            setLoading(false);
          }
        },

        prefill: {
          name: userDetails.name || "",
          email: userDetails.email || "",
          contact: userDetails.contact || "",
        },

        notes: {
          internalPaymentId: orderData.paymentId,
          subscriptionPlanId: String(planId),
          receipt: orderData.receipt,
        },

        theme: {
          color: "#6c5ce7",
        },

        modal: {
          ondismiss: function () {
            setLoading(false);

            const message =
              "Payment window was closed before completion.";

            setErrorMessage(message);

            if (onPaymentFailure) {
              onPaymentFailure(message);
            }
          },
        },
      };

      const razorpayCheckout =
        new window.Razorpay(options);

      razorpayCheckout.on(
        "payment.failed",
        async function (failureResponse) {
          const message =
            failureResponse?.error?.description ||
            "Razorpay payment failed.";

          const razorpayOrderId =
            failureResponse?.error?.metadata?.order_id ||
            orderData.razorpayOrderId;

          const razorpayPaymentId =
            failureResponse?.error?.metadata?.payment_id ||
            null;

          try {
            await markPaymentFailed({
              razorpayOrderId,
              razorpayPaymentId,
              failureReason: message,
            });
          } catch (recordFailureError) {
            console.error(
              "Unable to record failed payment:",
              recordFailureError
            );
          } finally {
            setErrorMessage(message);
            setLoading(false);

            if (onPaymentFailure) {
              onPaymentFailure(message);
            }
          }
        }
      );

      razorpayCheckout.open();
    } catch (error) {
      const message =
        error.message || "Unable to initiate payment.";

      setErrorMessage(message);
      setLoading(false);

      if (onPaymentFailure) {
        onPaymentFailure(message);
      }
    }
  };

  return (
    <div className="razorpay-checkout">
      <button
        type="button"
        className="razorpay-checkout-button"
        onClick={handlePayment}
        disabled={disabled || loading}
      >
        {loading ? "Processing..." : buttonText}
      </button>

      {errorMessage && (
        <p className="razorpay-checkout-error">
          {errorMessage}
        </p>
      )}
    </div>
  );
}

export default RazorpayCheckout;