import api from "./api";

// Tüm belgeleri getirir.
const getAllDocuments = async () => {

    const response = await api.get("/documents");

    return response.data;

};

// ID'ye göre belge getirir.
const getDocumentById = async (id) => {

    const response = await api.get(`/documents/${id}`);

    return response.data;

};

// Yeni belge yükler.
const uploadDocument = async (formData) => {

    const response = await api.post(
        "/documents",
        formData,
        {
            headers: {
                "Content-Type": "multipart/form-data"
            }
        }
    );

    return response.data;

};

// Belge indirir.
const downloadDocument = async (id) => {

    const response = await api.get(
        `/documents/${id}/download`,
        {
            responseType: "blob"
        }
    );

    return response;

};

// Belgeyi siler.
const deleteDocument = async (id) => {

    await api.delete(`/documents/${id}`);

};
const updateDocument = async (id, formData) => {

    const response = await api.put(

        `/documents/${id}`,

        formData,

        {

            headers: {

                "Content-Type": "multipart/form-data"

            }

        }

    );

    return response.data;

};

export default {

    getAllDocuments,
    getDocumentById,
    uploadDocument,
    updateDocument,
    downloadDocument,
    deleteDocument

};