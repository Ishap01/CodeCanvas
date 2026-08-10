import React, {
    useEffect,
    useMemo,
    useState,
} from "react";

import {
    useNavigate,
    useParams,
} from "react-router-dom";

import {
    FaArrowLeft,
    FaCode,
    FaImage,
    FaPlus,
    FaSave,
    FaTimes,
    FaTrash,
    FaUpload,
} from "react-icons/fa";

import {
    deleteSnippetImage,
    getSnippetById,
    replaceSnippetImage,
    updateSnippet,
    uploadSnippetImage,
} from "../../../services/snippetService";

import "./EditSnippet.css";

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

const INITIAL_FORM_DATA = {
    title: "",
    description: "",

    files: [
        {
            filename: "",
            code: "",
        },
    ],

    language: "",
    framework: "",
    category: "",
    visibility: "PUBLIC",
};


const MAX_IMAGE_SIZE =
    5 * 1024 * 1024;

function EditSnippet() {

    const { snippetId } = useParams();

    const navigate = useNavigate();

    const [formData, setFormData] =
        useState(INITIAL_FORM_DATA);

    const [tags, setTags] =
        useState([]);

    const [tagInput, setTagInput] =
        useState("");

    const [
        existingImageUrl,
        setExistingImageUrl,
    ] = useState("");

    const [
        selectedImage,
        setSelectedImage,
    ] = useState(null);

    const [
        selectedImagePreview,
        setSelectedImagePreview,
    ] = useState("");

    const [isLoading, setIsLoading] =
        useState(true);

    const [
        isSaving,
        setIsSaving,
    ] = useState(false);

    const [
        isUpdatingImage,
        setIsUpdatingImage,
    ] = useState(false);

    const [
        isDeletingImage,
        setIsDeletingImage,
    ] = useState(false);

    const [
        fieldErrors,
        setFieldErrors,
    ] = useState({});

    const [
        errorMessage,
        setErrorMessage,
    ] = useState("");

    const [
        successMessage,
        setSuccessMessage,
    ] = useState("");

    const selectedVisibility =
        useMemo(() => {

            return VISIBILITY_OPTIONS.find(
                (option) =>
                    option.value ===
                    formData.visibility
            );

        }, [formData.visibility]);

    useEffect(() => {

        const loadSnippet = async () => {

            if (!snippetId) {
                setErrorMessage(
                    "Snippet ID is missing."
                );

                setIsLoading(false);
                return;
            }

            try {
                setIsLoading(true);
                setErrorMessage("");

                const response =
                    await getSnippetById(
                        snippetId
                    );

                setFormData({

                    title: response?.title || "",

                    description: response?.description || "",

                    files:
                        response?.files?.length
                            ? response.files
                            : [
                                {
                                    filename: "",
                                    code: "",
                                },
                            ],

                    language: response?.language || "",

                    framework: response?.framework || "",

                    category: response?.categoryName || "",

                    visibility:
                        response?.visibility || "PUBLIC",

                });

                setTags(
                    Array.isArray(
                        response?.tags
                    )
                        ? response.tags
                        : []
                );

                setExistingImageUrl(
                    response?.previewImageUrl ||
                    ""
                );

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to load snippet."
                );

            } finally {
                setIsLoading(false);
            }
        };

        loadSnippet();

    }, [snippetId]);

    useEffect(() => {

        return () => {

            if (selectedImagePreview) {
                URL.revokeObjectURL(
                    selectedImagePreview
                );
            }
        };

    }, [selectedImagePreview]);

    const handleInputChange = (
        event
    ) => {

        const {
            name,
            value,
        } = event.target;

        setFormData(
            (previousData) => ({
                ...previousData,
                [name]: value,
            })
        );

        setFieldErrors(
            (previousErrors) => ({
                ...previousErrors,
                [name]: "",
            })
        );

        setErrorMessage("");
    };

    const handleFileChange = (index, field, value) => {

        setFormData((previous) => {

            const updatedFiles = [...previous.files];

            updatedFiles[index] = {
                ...updatedFiles[index],
                [field]: value,
            };

            return {
                ...previous,
                files: updatedFiles,
            };
        });

    };

    const addFile = () => {

        setFormData((previous) => ({

            ...previous,

            files: [

                ...previous.files,

                {
                    filename: "",
                    code: "",
                }

            ]

        }));

    };

    const removeFile = (index) => {

        if (formData.files.length == 1) {
            return;
        }

        setFormData((previous) => ({

            ...previous,

            files: previous.files.filter(
                (_, i) => i !== index
            )

        }));

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

        const alreadyExists =
            tags.some(
                (tag) =>
                    tag.toLowerCase() ===
                    normalizedTag.toLowerCase()
            );

        if (alreadyExists) {

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

        setTags(
            (previousTags) => [
                ...previousTags,
                normalizedTag,
            ]
        );

        setTagInput("");

        setFieldErrors(
            (previousErrors) => ({
                ...previousErrors,
                tags: "",
            })
        );
    };

    const handleTagKeyDown = (
        event
    ) => {

        if (
            event.key === "Enter" ||
            event.key === ","
        ) {
            event.preventDefault();
            addTag();
        }
    };

    const removeTag = (
        tagToRemove
    ) => {

        setTags(
            (previousTags) =>
                previousTags.filter(
                    (tag) =>
                        tag !== tagToRemove
                )
        );
    };

    const handleImageChange = (
        event
    ) => {

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
            !file.type.startsWith(
                "image/"
            )
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

        if (
            file.size >
            MAX_IMAGE_SIZE
        ) {

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

        if (
            selectedImagePreview
        ) {
            URL.revokeObjectURL(
                selectedImagePreview
            );
        }

        setSelectedImage(file);

        setSelectedImagePreview(
            URL.createObjectURL(file)
        );
    };

    const clearSelectedImage =
        () => {

            if (
                selectedImagePreview
            ) {
                URL.revokeObjectURL(
                    selectedImagePreview
                );
            }

            setSelectedImage(null);
            setSelectedImagePreview("");

            const input =
                document.getElementById(
                    "editSnippetImage"
                );

            if (input) {
                input.value = "";
            }
        };

    const validateForm = () => {

        const errors = {};

        if (
            !formData.title.trim()
        ) {
            errors.title =
                "Title is required.";
        }

        if (
            !formData.description.trim()
        ) {
            errors.description =
                "Description is required.";
        }

        formData.files.forEach((file, index) => {

            if (!file.filename.trim()) {

                errors[`filename_${index}`] =
                    "Filename is required.";

            }

            if (!file.code.trim()) {

                errors[`code_${index}`] =
                    "Code is required.";

            }

        });

        if (
            !formData.language.trim()
        ) {
            errors.language =
                "Language is required.";
        }

        if (
            !formData.category.trim()
        ) {
            errors.category =
                "Category is required.";
        }

        if (
            tags.length === 0
        ) {
            errors.tags =
                "At least one tag is required.";
        }

        if (
            !formData.visibility
        ) {
            errors.visibility =
                "Visibility is required.";
        }

        setFieldErrors(errors);

        return (
            Object.keys(errors).length ===
            0
        );
    };

    const showSuccessMessage = (
        message
    ) => {

        setSuccessMessage(message);

        window.setTimeout(() => {
            setSuccessMessage("");
        }, 3000);
    };

    const handleSubmit = async (
        event
    ) => {

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

            title: formData.title.trim(),

            description: formData.description.trim(),

            code: formData.files[0].code,

            filename: formData.files[0].filename,

            files: formData.files.map((file) => ({

                filename: file.filename.trim(),

                code: file.code,

            })),

            language: formData.language.trim(),

            framework:
                formData.framework.trim()
                    ? formData.framework.trim()
                    : null,

            category: formData.category.trim(),

            tags,

            visibility: formData.visibility,

        };

        try {
            setIsSaving(true);

            const response =
                await updateSnippet(
                    snippetId,
                    requestBody
                );

            setFormData({

                title:
                    response?.title ||
                    requestBody.title,

                description:
                    response?.description ||
                    requestBody.description,

                files:
                    response?.files?.length
                        ? response.files
                        : requestBody.files,

                language:
                    response?.language ||
                    requestBody.language,

                framework:
                    response?.framework || "",

                category:
                    response?.categoryName ||
                    requestBody.category,

                visibility:
                    response?.visibility ||
                    requestBody.visibility,

            });

            setTags(
                Array.isArray(
                    response?.tags
                )
                    ? response.tags
                    : tags
            );

            showSuccessMessage(
                "Snippet details updated successfully."
            );

        } catch (error) {

            setErrorMessage(
                error.message ||
                "Unable to update snippet."
            );

        } finally {
            setIsSaving(false);
        }
    };

    const handleSaveImage =
        async () => {

            if (
                !selectedImage ||
                isUpdatingImage
            ) {
                return;
            }

            try {
                setIsUpdatingImage(true);
                setErrorMessage("");

                const response =
                    existingImageUrl
                        ? await replaceSnippetImage(
                            snippetId,
                            selectedImage
                        )
                        : await uploadSnippetImage(
                            snippetId,
                            selectedImage
                        );

                setExistingImageUrl(
                    response?.previewImageUrl ||
                    selectedImagePreview
                );

                clearSelectedImage();

                showSuccessMessage(
                    existingImageUrl
                        ? "Preview image replaced successfully."
                        : "Preview image uploaded successfully."
                );

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to update preview image."
                );

            } finally {
                setIsUpdatingImage(false);
            }
        };

    const handleDeleteImage =
        async () => {

            if (
                !existingImageUrl ||
                isDeletingImage
            ) {
                return;
            }

            const confirmed =
                window.confirm(
                    "Delete the current preview image?"
                );

            if (!confirmed) {
                return;
            }

            try {
                setIsDeletingImage(true);
                setErrorMessage("");

                const response =
                    await deleteSnippetImage(
                        snippetId
                    );

                setExistingImageUrl("");

                showSuccessMessage(
                    response?.message ||
                    "Preview image deleted successfully."
                );

            } catch (error) {

                setErrorMessage(
                    error.message ||
                    "Unable to delete preview image."
                );

            } finally {
                setIsDeletingImage(false);
            }
        };

    if (isLoading) {

        return (
            <main className="editSnippetPage">

                <div className="editSnippetLoadingState">

                    <span />

                    <p>
                        Loading snippet...
                    </p>

                </div>

            </main>
        );
    }

    return (
        <main className="editSnippetPage">

            <div className="editSnippetContainer">

                <button
                    type="button"
                    className="editSnippetBackButton"
                    onClick={() =>
                        navigate(-1)
                    }
                >
                    <FaArrowLeft />

                    Back
                </button>

                <header className="editSnippetHeader">

                    <span className="editSnippetHeaderIcon">
                        <FaCode />
                    </span>

                    <div>
                        <p className="editSnippetEyebrow">
                            UPDATE YOUR CODE
                        </p>

                        <h1>
                            Edit snippet
                        </h1>

                        <p>
                            Update snippet details,
                            tags, visibility and
                            preview image.
                        </p>
                    </div>

                </header>

                {errorMessage && (
                    <div
                        className="editSnippetAlert editSnippetErrorAlert"
                        role="alert"
                    >
                        {errorMessage}
                    </div>
                )}

                {successMessage && (
                    <div
                        className="editSnippetAlert editSnippetSuccessAlert"
                        role="status"
                    >
                        {successMessage}
                    </div>
                )}

                <form
                    className="editSnippetForm"
                    onSubmit={handleSubmit}
                    noValidate
                >

                    <section className="editSnippetCard">

                        <div className="editSnippetSectionHeader">

                            <span>01</span>

                            <div>
                                <h2>
                                    Basic information
                                </h2>

                                <p>
                                    Update the title and
                                    explanation for this
                                    snippet.
                                </p>
                            </div>

                        </div>

                        <div className="editSnippetField">

                            <label htmlFor="editSnippetTitle">
                                Title
                                <span>*</span>
                            </label>

                            <input
                                id="editSnippetTitle"
                                type="text"
                                name="title"
                                value={formData.title}
                                onChange={
                                    handleInputChange
                                }
                                maxLength={200}
                                disabled={isSaving}
                                className={
                                    fieldErrors.title
                                        ? "editSnippetInputError"
                                        : ""
                                }
                            />

                            <div className="editSnippetFieldFooter">

                                {fieldErrors.title ? (
                                    <small className="editSnippetFieldError">
                                        {
                                            fieldErrors.title
                                        }
                                    </small>
                                ) : (
                                    <small>
                                        Maximum 200
                                        characters.
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

                        <div className="editSnippetField">

                            <label htmlFor="editSnippetDescription">
                                Description
                                <span>*</span>
                            </label>

                            <textarea
                                id="editSnippetDescription"
                                name="description"
                                value={
                                    formData.description
                                }
                                onChange={
                                    handleInputChange
                                }
                                rows={5}
                                disabled={isSaving}
                                className={
                                    fieldErrors.description
                                        ? "editSnippetInputError"
                                        : ""
                                }
                            />

                            {fieldErrors.description && (
                                <small className="editSnippetFieldError">
                                    {
                                        fieldErrors.description
                                    }
                                </small>
                            )}

                        </div>

                    </section>

                    <section className="editSnippetCard">

                        <div className="editSnippetSectionHeader">

                            <span>02</span>

                            <div>

                                <h2>Source code</h2>

                                <p>
                                    Update one or more source files stored inside this snippet.
                                </p>

                            </div>

                        </div>

                        {formData.files.map((file, index) => (

                            <div
                                key={index}
                                className="createSnippetFileCard"
                            >

                                <div className="createSnippetFileHeader">

                                    <h3>
                                        File {index + 1}
                                    </h3>

                                    {formData.files.length > 1 && (

                                        <button
                                            type="button"
                                            className="createSnippetRemoveFileButton"
                                            onClick={() => removeFile(index)}
                                        >
                                            <FaTimes />
                                            Remove
                                        </button>

                                    )}

                                </div>

                                <div className="editSnippetField">

                                    <label>
                                        Filename
                                        <span>*</span>
                                    </label>

                                    <input
                                        type="text"
                                        value={file.filename}
                                        onChange={(e) =>
                                            handleFileChange(
                                                index,
                                                "filename",
                                                e.target.value
                                            )
                                        }
                                        className={
                                            fieldErrors[`filename_${index}`]
                                                ? "editSnippetInputError"
                                                : ""
                                        }
                                    />

                                    {fieldErrors[`filename_${index}`] && (

                                        <small className="editSnippetFieldError">
                                            {fieldErrors[`filename_${index}`]}
                                        </small>

                                    )}

                                </div>

                                <div className="editSnippetField">

                                    <label>
                                        Source Code
                                        <span>*</span>
                                    </label>

                                    <textarea
                                        rows={18}
                                        spellCheck="false"
                                        value={file.code}
                                        onChange={(e) =>
                                            handleFileChange(
                                                index,
                                                "code",
                                                e.target.value
                                            )
                                        }
                                        className={`editSnippetCodeInput ${fieldErrors[`code_${index}`]
                                                ? "editSnippetInputError"
                                                : ""
                                            }`}
                                    />

                                    {fieldErrors[`code_${index}`] && (

                                        <small className="editSnippetFieldError">
                                            {fieldErrors[`code_${index}`]}
                                        </small>

                                    )}

                                </div>

                            </div>

                        ))}

                        <button
                            type="button"
                            className="createSnippetAddFileButton"
                            onClick={addFile}
                        >
                            <FaPlus />
                            Add Another File
                        </button>

                    </section>

                    <section className="editSnippetCard">

                        <div className="editSnippetSectionHeader">

                            <span>03</span>

                            <div>
                                <h2>
                                    Technology details
                                </h2>

                                <p>
                                    Update language,
                                    framework, category and
                                    tags.
                                </p>
                            </div>

                        </div>

                        <div className="editSnippetTwoColumns">

                            <div className="editSnippetField">

                                <label htmlFor="editSnippetLanguage">
                                    Language
                                    <span>*</span>
                                </label>

                                <select
                                    id="editSnippetLanguage"
                                    name="language"
                                    value={
                                        formData.language
                                    }
                                    onChange={
                                        handleInputChange
                                    }
                                    disabled={isSaving}
                                    className={
                                        fieldErrors.language
                                            ? "editSnippetInputError"
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
                                    <small className="editSnippetFieldError">
                                        {
                                            fieldErrors.language
                                        }
                                    </small>
                                )}

                            </div>

                            <div className="editSnippetField">

                                <label htmlFor="editSnippetFramework">
                                    Framework
                                </label>

                                <input
                                    id="editSnippetFramework"
                                    type="text"
                                    name="framework"
                                    value={
                                        formData.framework
                                    }
                                    onChange={
                                        handleInputChange
                                    }
                                    maxLength={100}
                                    disabled={isSaving}
                                    placeholder="Optional"
                                />

                            </div>

                            <div className="editSnippetField">

                                <label htmlFor="editSnippetCategory">
                                    Category
                                    <span>*</span>
                                </label>

                                <input
                                    id="editSnippetCategory"
                                    type="text"
                                    name="category"
                                    value={
                                        formData.category
                                    }
                                    onChange={
                                        handleInputChange
                                    }
                                    disabled={isSaving}
                                    className={
                                        fieldErrors.category
                                            ? "editSnippetInputError"
                                            : ""
                                    }
                                />

                                {fieldErrors.category && (
                                    <small className="editSnippetFieldError">
                                        {
                                            fieldErrors.category
                                        }
                                    </small>
                                )}

                            </div>

                        </div>

                        <div className="editSnippetField">

                            <label htmlFor="editSnippetTag">
                                Tags
                                <span>*</span>
                            </label>

                            <div className="editSnippetTagInputRow">

                                <input
                                    id="editSnippetTag"
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
                                    maxLength={50}
                                    placeholder="Type a tag and press Enter"
                                    disabled={isSaving}
                                    className={
                                        fieldErrors.tags
                                            ? "editSnippetInputError"
                                            : ""
                                    }
                                />

                                <button
                                    type="button"
                                    onClick={addTag}
                                    disabled={isSaving}
                                >
                                    <FaPlus />

                                    Add tag
                                </button>

                            </div>

                            {tags.length > 0 && (
                                <div className="editSnippetTags">

                                    {tags.map((tag) => (
                                        <span
                                            key={tag}
                                        >
                                            #{tag}

                                            <button
                                                type="button"
                                                onClick={() =>
                                                    removeTag(
                                                        tag
                                                    )
                                                }
                                                disabled={
                                                    isSaving
                                                }
                                                aria-label={`Remove ${tag}`}
                                            >
                                                <FaTimes />
                                            </button>
                                        </span>
                                    ))}

                                </div>
                            )}

                            {fieldErrors.tags ? (
                                <small className="editSnippetFieldError">
                                    {
                                        fieldErrors.tags
                                    }
                                </small>
                            ) : (
                                <small>
                                    Add between 1 and 10
                                    tags.
                                </small>
                            )}

                        </div>

                    </section>

                    <section className="editSnippetCard">

                        <div className="editSnippetSectionHeader">

                            <span>04</span>

                            <div>
                                <h2>
                                    Visibility
                                </h2>

                                <p>
                                    Change who can access
                                    this snippet.
                                </p>
                            </div>

                        </div>

                        <div className="editSnippetVisibilityGrid">

                            {VISIBILITY_OPTIONS.map(
                                (option) => (
                                    <label
                                        key={
                                            option.value
                                        }
                                        className={`editSnippetVisibilityOption ${formData.visibility ===
                                                option.value
                                                ? "editSnippetVisibilityOptionActive"
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
                                                isSaving
                                            }
                                        />

                                        <span className="editSnippetCustomRadio" />

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

                        <p className="editSnippetVisibilityNotice">
                            Selected visibility:{" "}
                            <strong>
                                {
                                    selectedVisibility
                                        ?.label
                                }
                            </strong>
                        </p>

                    </section>

                    <section className="editSnippetCard editSnippetImageCard">

                    <div className="editSnippetSectionHeader">

                        <span>05</span>

                        <div>
                            <h2>
                                Preview image
                            </h2>

                            <p>
                                Upload, replace or delete
                                the snippet preview image.
                            </p>
                        </div>

                    </div>

                    {existingImageUrl && (
                        <div className="editSnippetExistingImage">

                            <img
                                src={existingImageUrl}
                                alt="Current snippet preview"
                            />

                            <div className="editSnippetExistingImageActions">

                                <span>
                                    Current preview image
                                </span>

                                <button
                                    type="button"
                                    onClick={
                                        handleDeleteImage
                                    }
                                    disabled={
                                        isDeletingImage
                                    }
                                >
                                    {isDeletingImage ? (
                                        <>
                                            <span className="editSnippetSpinner" />

                                            Deleting...
                                        </>
                                    ) : (
                                        <>
                                            <FaTrash />

                                            Delete image
                                        </>
                                    )}
                                </button>

                            </div>

                        </div>
                    )}

                    {!selectedImagePreview ? (
                        <label
                            htmlFor="editSnippetImage"
                            className={`editSnippetImageDropZone ${fieldErrors.image
                                    ? "editSnippetImageDropZoneError"
                                    : ""
                                }`}
                        >
                            <FaImage />

                            <strong>
                                {existingImageUrl
                                    ? "Select replacement image"
                                    : "Select preview image"}
                            </strong>

                            <span>
                                PNG, JPG, JPEG or WEBP,
                                maximum 5 MB
                            </span>

                            <span className="editSnippetChooseImageButton">
                                <FaUpload />

                                Choose image
                            </span>
                        </label>
                    ) : (
                        <div className="editSnippetSelectedImage">

                            <img
                                src={
                                    selectedImagePreview
                                }
                                alt="Selected replacement preview"
                            />

                            <div className="editSnippetSelectedImageOverlay">

                                <span>
                                    {
                                        selectedImage
                                            ?.name
                                    }
                                </span>

                                <button
                                    type="button"
                                    onClick={
                                        clearSelectedImage
                                    }
                                    disabled={
                                        isUpdatingImage
                                    }
                                >
                                    <FaTimes />

                                    Remove selection
                                </button>

                            </div>

                        </div>
                    )}

                    <input
                        id="editSnippetImage"
                        type="file"
                        accept="image/png,image/jpeg,image/jpg,image/webp"
                        onChange={
                            handleImageChange
                        }
                        hidden
                    />

                    {fieldErrors.image && (
                        <small className="editSnippetFieldError">
                            {
                                fieldErrors.image
                            }
                        </small>
                    )}

                    {selectedImage && (
                        <button
                            type="button"
                            className="editSnippetUpdateImageButton"
                            onClick={
                                handleSaveImage
                            }
                            disabled={
                                isUpdatingImage
                            }
                        >
                            {isUpdatingImage ? (
                                <>
                                    <span className="editSnippetSpinner" />

                                    Uploading image...
                                </>
                            ) : (
                                <>
                                    <FaUpload />

                                    {existingImageUrl
                                        ? "Replace image"
                                        : "Upload image"}
                                </>
                            )}
                        </button>
                    )}

                </section>

                    <div className="editSnippetStickyActions">

                        <button
                            type="button"
                            onClick={() =>
                                navigate(
                                    `/snippets/${snippetId}`
                                )
                            }
                            disabled={isSaving}
                            className="editSnippetCancelButton"
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            disabled={isSaving}
                            className="editSnippetSaveButton"
                        >
                            {isSaving ? (
                                <>
                                    <span className="editSnippetSpinner" />

                                    Saving...
                                </>
                            ) : (
                                <>
                                    <FaSave />

                                    Save changes
                                </>
                            )}
                        </button>

                    </div>

                </form>

                

            </div>

        </main>
    );
}

export default EditSnippet;