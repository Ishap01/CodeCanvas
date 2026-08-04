import "./Card.css";

const Card = ({
    children,
    className = "",
    padding = "md",
    hover = false
}) => {

    return (

        <div
            className={`cc-card cc-card-${padding} ${hover ? "cc-card-hover" : ""} ${className}`}
        >
            {children}
        </div>

    );

};

export default Card;