import { View, Text, Button } from "react-native";
import React, { useEffect, useState } from "react";
import { useLocalSearchParams } from "expo-router";
import * as Linking from "expo-linking";
import axios from "axios";

const Page = () => {
  const { token } = useLocalSearchParams<{ token: string }>();
  const url = Linking.useURL();
  const [user, setUser] = useState<any>(null);

  function getUser() {
    const options = {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    };
    axios.get("http://127.0.0.1:8080/user", options).then((response) => {
      if (response.data) {
        setUser(response.data);
      } else {
        console.log("Didnt work");
      }
    });
  }

  return (
    <View>
      <Text>
        Received token {token} with url {url}
      </Text>

      <Button onPress={getUser} title="Get User" />

      <Text style={{ fontSize: 28 }}>{user?.username}</Text>
    </View>
  );
};

export default Page;
