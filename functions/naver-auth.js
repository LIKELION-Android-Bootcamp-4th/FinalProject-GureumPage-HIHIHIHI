const admin = require("firebase-admin");
const axios = require("axios");
const {mapNaverUser} = require("./utils/social-mapper");
const {createOrUpdateUserDoc} = require("./create-or-update-user-doc");


module.exports.handler = async (request) => {
  try {
    const accessToken = request.data.accessToken;
    if (!accessToken) {
      throw new Error("accessToken is required");
    }

    const res = await axios.get("https://openapi.naver.com/v1/nid/me", {
      headers: {Authorization: `Bearer ${accessToken}`},
    });

    const user = mapNaverUser(res.data);
    const customToken = await admin.auth().createCustomToken(user.uid, user);

    await createOrUpdateUserDoc({
      uid: user.uid,
      provider: "naver",
    });

    return {token: customToken};
  } catch (error) {
    console.error("naverCustomAuth error:", error);
    throw new Error("Failed to authenticate with Naver");
  }
};
