import React, {
    useEffect,
    useMemo,
    useState,
} from "react";

import {
    useNavigate,
} from "react-router-dom";

import {
    FaArrowLeft,
    FaCode,
    FaImage,
    FaPlus,
    FaTimes,
    FaUpload,
} from "react-icons/fa";

import {
    createSnippet,
    uploadSnippetImage,
} from "../../../services/snippetService";

import "./CreateSnippet.css";

const INITIAL_FORM_DATA = {
    title: "",
    description: "",
    code: "",
    language: "",
    framework: "",
    category: "",
    visibility: "PUBLIC",
};

const LANGUAGE_OPTIONS = [
    "Java",
    "JavaScript",
    "TypeScript",
    "Python",
    "C",
    "C++",
    "C#",
    "PHP",
    "Go",
    "Kotlin",
    "Swift",
    "Ruby",
    "Rust",
    "HTML",
    "CSS",
    "SQL",
    "Other",
];

const VISIBILITY_OPTIONS = [
    {
        value: "PUBLIC",
        label: "Public",
        description:
            "Anyone can view this snippet.",
    },
    {
        value: "PREMIUM",
        label: "Premium",
        description:
            "Only premium users can view this snippet.",
    },
    {
        value: "PRIVATE",
        label: "Private",
        description:
            "Only you can view this snippet.",
    },
];

const MAX_IMAGE_SIZE =
    5 * 1024 * 1024;

function CreateSnippet() {

    const navigate = useNavigate();

    const [formData, setFormData] =
        useState(INITIAL_FORM_DATA);

    const [tagInput, setTagInput] =
        useState("");

    const [tags, setTags] =
        useState([]);

    const [selectedImage, setSelectedImage] =
        useState(null);

    const [imagePreviewUrl, setImagePreviewUrl] =
        useState("");

    const [fieldErrors, setFieldErrors] =
        useState({});

    const [errorMessage, setErrorMessage] =
        useState("");

    const [successMessage, setSuccessMessage] =
        useState("");

    const [isSubmitting, setIsSubmitting] =
        useState(false);

    const selectedVisibility = useMemo(
        () => {
            return VISIBILITY_OPTIONS.find(
                (option) =>
                    option.value ===
                    formData.visibility
            );
        },
        [formData.visibility]
    );

    useEffect(() => {
        return () => {
            if (imagePreviewUrl) {
                URL.revokeObjectURL(
                    imagePreviewUrl
                );
            }
        };
    }, [imagePreviewUrl]);

    const handleInputChange = (event) => {
        const {
            name,
            value,
        } = event.target;

        setFormData((previousData) => ({
            ...previousData,
            [name]: value,
        }));

        setFieldErrors((previousErrors) => ({
            ...previousErrors,
            [name]: "",
        }));

        setErrorMessage("");
    };

    const addTag = () => {
        const normalizedTag =
            tagInput.trim();

        if (!normalizedTag) {
            setFieldErrors(
                (previousErrors) => ({
                    ...previousErrors,
                    tags:
                        "Enter a tag before adding it.",
                })
            );

            return;
        }

        const tagAlreadyExists =
            tags.some(
                (existingTag) =>
                    existingTag.toLowerCase() ===
                    normalizedTag.toLowerCase()
            );

        if (tagAlreadyExists) {
            setFieldErrors(
                (previousErrors) => ({
                    ...previousErrors,
                    tags:
                        "This tag is already added.",
                })
            );

            return;
        }

        if (tags.length >= 10) {
            setFieldErrors(
                (previousErrors) => ({
                    ...previousErrors,
                    tags:
                        "You can add a maximum of 10 tags.",
                })
            );

            return;
        }

        setTags((previousTags) => [
            ...previousTags,
            normalizedTag,
        ]);

        setTagInput("");

        setFieldErrors(
            (previousErrors) => ({
                ...previousErrors,
                tags: "",
            })
        );
    };

    const handleTagKeyDown = (event) => {
        if (
            event.key === "Enter" ||
            event.key === ","
        ) {
            event.preventDefault();
            addTag();
        }
    };

    const removeTag = (tagToRemove) => {
        setTags((previousTags) =>
            previousTags.filter(
                (tag) =>
                    tag !== tagToRemove
            )
        );
    };

    const handleImageChange = (event) => {
        const file =
            event.target.files?.[0];

        setFieldErrors(
            (previousErrors) => ({
                ...previousErrors,
                image: "",
            })
        );

        if (!file) {
            return;
        }

        if (
            !file.type.startsWith("image/")
        ) {
            event.target.value = "";

            setFieldErrors(
                (previousErrors) => ({
                    ...previousErrors,
                    image:
                        "Please select a valid image file.",
                })
            );

            return;
        }

        if (file.size > MAX_IMAGE_SIZE) {
            event.target.value = "";

            setFieldErrors(
                (previousErrors) => ({
                    ...previousErrors,
                    image:
                        "Image size must not exceed 5 MB.",
                })
            );

            return;
        }

        if (imagePreviewUrl) {
            URL.revokeObjectURL(
                imagePreviewUrl
            );
        }

        const newPreviewUrl =
            URL.createObjectURL(file);

        setSelectedImage(file);
        setImagePreviewUrl(
            newPreviewUrl
        );
    };

    const removeSelectedImage = () => {
        if (imagePreviewUrl) {
            URL.revokeObjectURL(
                imagePreviewUrl
            );
        }

        setSelectedImage(null);
        setImagePreviewUrl("");

        const imageInput =
            document.getElementById(
                "snippetPreviewImage"
            );

        if (imageInput) {
            imageInput.value = "";
        }
    };

    const validateForm = () => {
        const errors = {};

        if (!formData.title.trim()) {
            errors.title =
                "Title is required.";
        }

        if (
            !formData.description.trim()
        ) {
            errors.description =
                "Description is required.";
        }

        if (!formData.code.trim()) {
            errors.code =
                "Code is required.";
        }

        if (!formData.language.trim()) {
            errors.language =
                "Language is required.";
        }

        if (!formData.category.trim()) {
            errors.category =
                "Category is required.";
        }

        if (tags.length === 0) {
            errors.tags =
                "At least one tag is required.";
        }

        if (!formData.visibility) {
            errors.visibility =
                "Visibility is required.";
        }

        setFieldErrors(errors);

        return (
            Object.keys(errors).length ===
            0
        );
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        setErrorMessage("");
        setSuccessMessage("");

        if (!validateForm()) {
            setErrorMessage(
                "Please correct the highlighted fields."
            );

            return;
        }

        const requestBody = {
            title:
                formData.title.trim(),

            description:
                formData.description.trim(),

            code:
                formData.code,

            language:
                formData.language.trim(),

            framework:
                formData.framework.trim()
                    ? formData.framework.trim()
                    : null,

            category:
                formData.category.trim(),

            tags,

            visibility:
                formData.visibility,
        };

        try {
            setIsSubmitting(true);

            /*
             * Request 1:
             * Snippet details create honge.
             */
            const createdSnippet =
                await createSnippet(
                    requestBody
                );

            if (
                !createdSnippet?.snippetId
            ) {
                throw new Error(
                    "Snippet was created, but its ID was not returned."
                );
            }

            /*
             * Request 2:
             * Image selected ho to separate
             * multipart request jayegi.
             */
            if (selectedImage) {
                await uploadSnippetImage(
                    createdSnippet.snippetId,
                    selectedImage
                );
            }

            setSuccessMessage(
                selectedImage
                    ? "Snippet and preview image created successfully."
                    : "Snippet created successfully."
            );

            window.setTimeout(() => {
                navigate(
                    `/snippets/${createdSnippet.snippetId}`
                );
            }, 700);

        } catch (error) {
            setErrorMessage(
                error.message ||
                    "Unable to create snippet."
            );

        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <main className="createSnippetPage">

            <div className="createSnippetContainer">

                <div className="createSnippetHeader">

                    <button
                        type="button"
                        className="createSnippetBackButton"
                        onClick={() =>
                            navigate(-1)
                        }
                        disabled={isSubmitting}
                    >
                        <FaArrowLeft />

                        Back
                    </button>

                    <div className="createSnippetHeading">

                        <span className="createSnippetHeadingIcon">
                            <FaCode />
                        </span>

                        <div>
                            <p className="createSnippetEyebrow">
                                CODECANVAS
                            </p>

                            <h1>
                                Create a new snippet
                            </h1>

                            <p>
                                Share reusable code,
                                ideas and solutions
                                with the community.
                            </p>
                        </div>

                    </div>

                </div>

                {errorMessage && (
                    <div
                        className="createSnippetAlert createSnippetErrorAlert"
                        role="alert"
                    >
                        {errorMessage}
                    </div>
                )}

                {successMessage && (
                    <div
                        className="createSnippetAlert createSnippetSuccessAlert"
                        role="status"
                    >
                        {successMessage}
                    </div>
                )}

                <form
                    className="createSnippetForm"
                    onSubmit={handleSubmit}
                    noValidate
                >

                    <section className="createSnippetCard">

                        <div className="createSnippetSectionHeader">
                            <span>01</span>

                            <div>
                                <h2>
                                    Basic information
                                </h2>

                                <p>
                                    Add a meaningful
                                    title and explain what
                                    the code does.
                                </p>
                            </div>
                        </div>

                        <div className="createSnippetField">

                            <label htmlFor="snippetTitle">
                                Title
                                <span>*</span>
                            </label>

                            <input
                                id="snippetTitle"
                                type="text"
                                name="title"
                                value={formData.title}
                                onChange={
                                    handleInputChange
                                }
                                placeholder="Example: JWT authentication filter"
                                maxLength={200}
                                disabled={isSubmitting}
                                className={
                                    fieldErrors.title
                                        ? "createSnippetInputError"
                                        : ""
                                }
                            />

                            <div className="createSnippetFieldFooter">

                                {fieldErrors.title ? (
                                    <small className="createSnippetFieldError">
                                        {
                                            fieldErrors.title
                                        }
                                    </small>
                                ) : (
                                    <small>
                                        Use a short,
                                        descriptive title.
                                    </small>
                                )}

                                <small>
                                    {
                                        formData.title
                                            .length
                                    }
                                    /200
                                </small>

                            </div>

                        </div>

                        <div className="createSnippetField">

                            <label htmlFor="snippetDescription">
                                Description
                                <span>*</span>
                            </label>

                            <textarea
                                id="snippetDescription"
                                name="description"
                                value={
                                    formData.description
                                }
                                onChange={
                                    handleInputChange
                                }
                                placeholder="Explain the problem solved by this snippet and how it can be used..."
                                rows={5}
                                disabled={isSubmitting}
                                className={
                                    fieldErrors.description
                                        ? "createSnippetInputError"
                                        : ""
                                }
                            />

                            {fieldErrors.description && (
                                <small className="createSnippetFieldError">
                                    {
                                        fieldErrors.description
                                    }
                                </small>
                            )}

                        </div>

                    </section>

                    <section className="createSnippetCard">

                        <div className="createSnippetSectionHeader">
                            <span>02</span>

                            <div>
                                <h2>
                                    Code
                                </h2>

                                <p>
                                    Paste the complete code
                                    that users should be
                                    able to view and copy.
                                </p>
                            </div>
                        </div>

                        <div className="createSnippetField">

                            <label htmlFor="snippetCode">
                                Source code
                                <span>*</span>
                            </label>

                            <textarea
                                id="snippetCode"
                                name="code"
                                value={formData.code}
                                onChange={
                                    handleInputChange
                                }
                                placeholder={`public class Example {\n    public static void main(String[] args) {\n        System.out.println("Hello CodeCanvas");\n    }\n}`}
                                rows={18}
                                spellCheck="false"
                                disabled={isSubmitting}
                                className={`createSnippetCodeInput ${
                                    fieldErrors.code
                                        ? "createSnippetInputError"
                                        : ""
                                }`}
                            />

                            {fieldErrors.code && (
                                <small className="createSnippetFieldError">
                                    {
                                        fieldErrors.code
                                    }
                                </small>
                            )}

                        </div>

                    </section>

                    <section className="createSnippetCard">

                        <div className="createSnippetSectionHeader">
                            <span>03</span>

                            <div>
                                <h2>
                                    Technology details
                                </h2>

                                <p>
                                    Help users discover the
                                    snippet through language,
                                    framework and category.
                                </p>
                            </div>
                        </div>

                        <div className="createSnippetTwoColumns">

                            <div className="createSnippetField">

                                <label htmlFor="snippetLanguage">
                                    Language
                                    <span>*</span>
                                </label>

                                <select
                                    id="snippetLanguage"
                                    name="language"
                                    value={
                                        formData.language
                                    }
                                    onChange={
                                        handleInputChange
                                    }
                                    disabled={
                                        isSubmitting
                                    }
                                    className={
                                        fieldErrors.language
                                            ? "createSnippetInputError"
                                            : ""
                                    }
                                >
                                    <option value="">
                                        Select language
                                    </option>

                                    {LANGUAGE_OPTIONS.map(
                                        (language) => (
                                            <option
                                                key={
                                                    language
                                                }
                                                value={
                                                    language
                                                }
                                            >
                                                {
                                                    language
                                                }
                                            </option>
                                        )
                                    )}
                                </select>

                                {fieldErrors.language && (
                                    <small className="createSnippetFieldError">
                                        {
                                            fieldErrors.language
                                        }
                                    </small>
                                )}

                            </div>

                            <div className="createSnippetField">

                                <label htmlFor="snippetFramework">
                                    Framework
                                </label>

                                <input
                                    id="snippetFramework"
                                    type="text"
                                    name="framework"
                                    value={
                                        formData.framework
                                    }
                                    onChange={
                                        handleInputChange
                                    }
                                    placeholder="Example: Spring Boot"
                                    maxLength={100}
                                    disabled={
                                        isSubmitting
                                    }
                                />

                                <small>
                                    Optional
                                </small>

                            </div>

                            <div className="createSnippetField">

                                <label htmlFor="snippetCategory">
                                    Category
                                    <span>*</span>
                                </label>

                                <input
                                    id="snippetCategory"
                                    type="text"
                                    name="category"
                                    value={
                                        formData.category
                                    }
                                    onChange={
                                        handleInputChange
                                    }
                                    placeholder="Example: Backend Development"
                                    disabled={
                                        isSubmitting
                                    }
                                    className={
                                        fieldErrors.category
                                            ? "createSnippetInputError"
                                            : ""
                                    }
                                />

                                {fieldErrors.category && (
                                    <small className="createSnippetFieldError">
                                        {
                                            fieldErrors.category
                                        }
                                    </small>
                                )}

                            </div>

                        </div>

                        <div className="createSnippetField">

                            <label htmlFor="snippetTags">
                                Tags
                                <span>*</span>
                            </label>

                            <div className="createSnippetTagInputRow">

                                <input
                                    id="snippetTags"
                                    type="text"
                                    value={tagInput}
                                    onChange={(event) => {
                                        setTagInput(
                                            event.target
                                                .value
                                        );

                                        setFieldErrors(
                                            (
                                                previousErrors
                                            ) => ({
                                                ...previousErrors,
                                                tags: "",
                                            })
                                        );
                                    }}
                                    onKeyDown={
                                        handleTagKeyDown
                                    }
                                    placeholder="Type a tag and press Enter"
                                    maxLength={50}
                                    disabled={
                                        isSubmitting
                                    }
                                    className={
                                        fieldErrors.tags
                                            ? "createSnippetInputError"
                                            : ""
                                    }
                                />

                                <button
                                    type="button"
                                    onClick={addTag}
                                    disabled={
                                        isSubmitting
                                    }
                                >
                                    <FaPlus />
                                    Add tag
                                </button>

                            </div>

                            {tags.length > 0 && (
                                <div className="createSnippetTags">

                                    {tags.map((tag) => (
                                        <span
                                            key={tag}
                                            className="createSnippetTag"
                                        >
                                            #{tag}

                                            <button
                                                type="button"
                                                onClick={() =>
                                                    removeTag(
                                                        tag
                                                    )
                                                }
                                                aria-label={`Remove ${tag} tag`}
                                                disabled={
                                                    isSubmitting
                                                }
                                            >
                                                <FaTimes />
                                            </button>
                                        </span>
                                    ))}

                                </div>
                            )}

                            {fieldErrors.tags ? (
                                <small className="createSnippetFieldError">
                                    {
                                        fieldErrors.tags
                                    }
                                </small>
                            ) : (
                                <small>
                                    Add between 1 and 10
                                    relevant tags.
                                </small>
                            )}

                        </div>

                    </section>

                    <section className="createSnippetCard">

                        <div className="createSnippetSectionHeader">
                            <span>04</span>

                            <div>
                                <h2>
                                    Visibility
                                </h2>

                                <p>
                                    Decide who can access
                                    this snippet.
                                </p>
                            </div>
                        </div>

                        <div className="createSnippetVisibilityGrid">

                            {VISIBILITY_OPTIONS.map(
                                (option) => (
                                    <label
                                        key={
                                            option.value
                                        }
                                        className={`createSnippetVisibilityOption ${
                                            formData.visibility ===
                                            option.value
                                                ? "createSnippetVisibilityOptionActive"
                                                : ""
                                        }`}
                                    >
                                        <input
                                            type="radio"
                                            name="visibility"
                                            value={
                                                option.value
                                            }
                                            checked={
                                                formData.visibility ===
                                                option.value
                                            }
                                            onChange={
                                                handleInputChange
                                            }
                                            disabled={
                                                isSubmitting
                                            }
                                        />

                                        <span className="createSnippetCustomRadio" />

                                        <span>
                                            <strong>
                                                {
                                                    option.label
                                                }
                                            </strong>

                                            <small>
                                                {
                                                    option.description
                                                }
                                            </small>
                                        </span>
                                    </label>
                                )
                            )}

                        </div>

                        <p className="createSnippetVisibilityNotice">
                            Selected visibility:{" "}
                            <strong>
                                {
                                    selectedVisibility?.label
                                }
                            </strong>
                        </p>

                    </section>

                    <section className="createSnippetCard">

                        <div className="createSnippetSectionHeader">
                            <span>05</span>

                            <div>
                                <h2>
                                    Preview image
                                </h2>

                                <p>
                                    Add an optional image
                                    that represents the
                                    snippet.
                                </p>
                            </div>
                        </div>

                        {!imagePreviewUrl ? (
                            <label
                                htmlFor="snippetPreviewImage"
                                className={`createSnippetImageDropZone ${
                                    fieldErrors.image
                                        ? "createSnippetImageDropZoneError"
                                        : ""
                                }`}
                            >
                                <FaImage />

                                <strong>
                                    Select a preview image
                                </strong>

                                <span>
                                    PNG, JPG, JPEG or WEBP,
                                    up to 5 MB
                                </span>

                                <span className="createSnippetImageSelectButton">
                                    <FaUpload />
                                    Choose image
                                </span>
                            </label>
                        ) : (
                            <div className="createSnippetImagePreview">

                                <img
                                    src={
                                        imagePreviewUrl
                                    }
                                    alt="Snippet preview"
                                />

                                <div className="createSnippetImagePreviewOverlay">

                                    <p>
                                        {
                                            selectedImage?.name
                                        }
                                    </p>

                                    <button
                                        type="button"
                                        onClick={
                                            removeSelectedImage
                                        }
                                        disabled={
                                            isSubmitting
                                        }
                                    >
                                        <FaTimes />
                                        Remove image
                                    </button>

                                </div>

                            </div>
                        )}

                        <input
                            id="snippetPreviewImage"
                            type="file"
                            accept="image/png,image/jpeg,image/jpg,image/webp"
                            onChange={
                                handleImageChange
                            }
                            disabled={isSubmitting}
                            hidden
                        />

                        {fieldErrors.image && (
                            <small className="createSnippetFieldError">
                                {
                                    fieldErrors.image
                                }
                            </small>
                        )}

                    </section>

                    <div className="createSnippetActions">

                        <button
                            type="button"
                            className="createSnippetCancelButton"
                            onClick={() =>
                                navigate(
                                    "/snippets"
                                )
                            }
                            disabled={isSubmitting}
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            className="createSnippetSubmitButton"
                            disabled={isSubmitting}
                        >
                            {isSubmitting ? (
                                <>
                                    <span className="createSnippetSpinner" />

                                    Creating snippet...
                                </>
                            ) : (
                                <>
                                    <FaCode />

                                    Create snippet
                                </>
                            )}
                        </button>

                    </div>

                </form>

            </div>

        </main>
    );
}

export default CreateSnippet;