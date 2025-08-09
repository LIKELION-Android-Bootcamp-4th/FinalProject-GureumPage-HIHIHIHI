const admin = require("firebase-admin");
const axios = require("axios");
const {mapKakaoUser} = require("./utils/social-mapper");
const {createOrUpdateUserDoc} = require("./create-or-update-user-doc");

module.exports.handler = async (request) => {
  try {
    const accessToken = request.data.accessToken;
    if (!accessToken) {
      throw new Error("accessToken is required");
    }

    const res = await axios.get("https://kapi.kakao.com/v2/user/me", {
      headers: {Authorization: `Bearer ${accessToken}`},
    });

    const user = mapKakaoUser(res.data);
    const customToken = await admin.auth().createCustomToken(user.uid, user);

    await createOrUpdateUserDoc({
      uid: user.uid,
      provider: "kakao",
    });

    return {token: customToken};
  } catch (error) {
    console.error("kakaoCustomAuth error:", error);
    throw new Error("Failed to authenticate with Kakao");
  }
};

