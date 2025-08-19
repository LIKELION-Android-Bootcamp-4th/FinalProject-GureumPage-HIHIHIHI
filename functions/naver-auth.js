const admin = require("firebase-admin");
const axios = require("axios");
const {v4: uuidv4} = require("uuid");
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
    const newUid = `${user.uid}_${uuidv4()}`;
    const customToken = await admin.auth().createCustomToken(newUid, user);

    await createOrUpdateUserDoc({
      uid: newUid,
      provider: "naver",
    });

    return {token: customToken};
  } catch (error) {
    console.error("naverCustomAuth error:", error);
    throw new Error("Failed to authenticate with Naver");
  }
};
