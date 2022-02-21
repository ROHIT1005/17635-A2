let authData = {};
let authTokens = {}

// module.exports = authData;
module.exports = {
    register: function(username, password) {
        if (!(username in authData)) {
            authData[username] = password;
            let token = "token:"+username;
            authTokens[token] = username;
            return token;
        }
        return "";
    },
    checkToken: function(token) {
        return token in authTokens;
    }
};