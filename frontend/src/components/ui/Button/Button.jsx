import "./Button.css";

const Button = ({
    children,
    variant = "primary",
    size = "md",
    type = "button",
    disabled = false,
    onClick,
    className = ""
}) => {

    return (
        <button
            type={type}
            disabled={disabled}
            onClick={onClick}
            className={`cc-btn cc-btn-${variant} cc-btn-${size} ${className}`}
        >
            {children}
        </button>
    );

};

export default Button;