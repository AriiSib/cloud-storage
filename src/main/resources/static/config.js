window.APP_CONFIG = {

    //link to your GitHub repository with the project
    githubLink: "https://github.com/AriiSib/cloud-storage",

    //The name that appears in the header
    mainName: "Cloud Storage",

    //your backing address. if it’s empty, it means it’s on the same url with the same port.
    //if you run the back and front via docker compose - here you put the name of the back in the docker network
    baseUrl: "",

    //API prefix of your back
    baseApi: "/api",


    /*
    *
    * Form Validation Configuration
    *
    * */

    //If true - the form will be validated,
    //errors will be displayed as you type. The button will be active only if the data is valid
    //If false, the form can be submitted without validation.
    validateLoginForm: true,
    validateRegistrationForm: true,

    //correct username
    validUsername: {
        minLength: 5,
        maxLength: 20,
        pattern: "^[a-zA-Zа-яА-Я0-9]+[a-zA-Zа-яА-Я_0-9]*[a-zA-Zа-яА-Я0-9]+$",
    },

    //correct password
    validPassword: {
        minLength: 6,
        maxLength: 32,
        pattern: "^[^\\s]+$",
    },

    //correct name for the folder
    validFolderName: {
        minLength: 1,
        maxLength: 200,
        pattern: "^(?!\\.{1,2}$)[a-zA-Zа-яА-Я0-9 _().\\[\\]{}+@-]+$",
    },


    /*
    *
    * Utility Configurations
    *
    * */

    //Whether to allow moving selected files and folders using drag and drop to adjacent folders. (drag n drop)
    isMoveAllowed: true,

    //Allow cutting and pasting files/folders. The /move endpoint is used for this - if you have it implemented, then everything should work
    isCutPasteAllowed: true,

    //Allow custom context menu for managing files (called with right mouse button - on one file, or on selected ones)
    isFileContextMenuAllowed: true,

    //Allow shortcuts on the page - Ctrl+X, Ctrl+V, Del - on selected elements
    isShortcutsAllowed: true,

    //a set of utility functions for interacting with the front.
    functions: {

        //function for mapping the backing data format to the front format.
        //If the backing is with Sergei’s format, you don’t have to change it.
        //What are the features of the FRONT format (if the back is different and you will implement your own functionality)
        //1) path in the data front must contain the full path to the object from the root folder.
        //   If the object is a folder, then path must contain a slash at the end
        //2) The same goes for name - if the object is a folder - there must be a slash at the end
        //   if your back gives obj.name for folders without a slash at the end - in this
        //  functions add a slash at the end for folders

        //This mapping assumes that obj.path from the back will come with a slash at the end.
        //If the object is in the root directory - obj.path is an empty string. and after formatting - path will be just the name of the object
        mapObjectToFrontFormat: (obj) => {
            return {
                lastModified: null,
                name: obj.name,
                size: obj.size,
                path: obj.path + obj.name, //the path in full format is necessary for correct navigation
                folder: obj.type === "DIRECTORY" //front uses a simple boolean. If the folder has a different name, change it
            }
        },

    }

};