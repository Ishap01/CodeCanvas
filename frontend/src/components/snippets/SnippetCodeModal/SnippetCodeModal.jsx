import React, {
    useEffect,
    useState,
} from "react";

import {
    FaCode,
    FaCopy,
    FaExternalLinkAlt,
    FaTimes,
} from "react-icons/fa";

import { Link } from "react-router-dom";

import "./SnippetCodeModal.css";

function SnippetCodeModal({
    snippet,
    isOpen,
    onClose,
}) {

    const [copied, setCopied] =
        useState(false);

    useEffect(() => {

        if (!isOpen) {
            return undefined;
        }

        const previousOverflow =
            document.body.style.overflow;

        document.body.style.overflow =
            "hidden";

        const handleKeyDown = (
            event
        ) => {

            if (event.key === "Escape") {
                onClose();
            }
        };

        window.addEventListener(
            "keydown",
            handleKeyDown
        );

        return () => {

            document.body.style.overflow =
                previousOverflow;

            window.removeEventListener(
                "keydown",
                handleKeyDown
            );
        };

    }, [isOpen, onClose]);

    if (!isOpen || !snippet) {
        return null;
    }

    const handleCopy = async () => {

        if (!snippet.code) {
            return;
        }

        try {

            await navigator.clipboard.writeText(
                snippet.code
            );

            setCopied(true);

            window.setTimeout(() => {
                setCopied(false);
            }, 1600);

        } catch (error) {

            console.error(
                "Unable to copy code:",
                error
            );
        }
    };

    return (
        <div
            className="snippetCodeModalBackdrop"
            role="presentation"
            onMouseDown={onClose}
        >

            <section
                className="snippetCodeModal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="snippetCodeModalTitle"
                onMouseDown={(event) =>
                    event.stopPropagation()
                }
            >

                <header className="snippetCodeModalHeader">

                    <div className="snippetCodeModalHeading">

                        <span className="snippetCodeModalIcon">
                            <FaCode />
                        </span>

                        <div>

                            <span>
                                {snippet.language ||
                                "Code"}
                            </span>

                            <h2 id="snippetCodeModalTitle">
                                {snippet.title}
                            </h2>

                        </div>

                    </div>

                    <button
                        type="button"
                        className="snippetCodeModalClose"
                        onClick={onClose}
                        aria-label="Close code"
                    >
                        <FaTimes />
                    </button>

                </header>

                <div className="snippetCodeModalMeta">

                    {snippet.framework && (
                        <span>
                            {snippet.framework}
                        </span>
                    )}

                    {snippet.categoryName && (
                        <span>
                            {snippet.categoryName}
                        </span>
                    )}

                    {snippet.visibility && (
                        <span>
                            {snippet.visibility}
                        </span>
                    )}

                </div>

                <div className="snippetCodeModalBody">

                    <pre>
                        <code>
                            {snippet.code ||
                            "// No code available"}
                        </code>
                    </pre>

                </div>

                <footer className="snippetCodeModalFooter">

                    <p>
                        {snippet.description}
                    </p>

                    <div>

                        <button
                            type="button"
                            onClick={handleCopy}
                        >
                            <FaCopy />

                            {copied
                                ? "Copied"
                                : "Copy code"}
                        </button>

                        <Link
                            to={`/snippets/${snippet.snippetId}`}
                            onClick={onClose}
                        >
                            <FaExternalLinkAlt />

                            Full details
                        </Link>

                    </div>

                </footer>

            </section>

        </div>
    );
}

export default SnippetCodeModal;