import { View, Text } from "react-native";
import React from "react";
import { Link } from "expo-router";

const Page = () => {
  return (
    <View>
      <Text>Welcome!</Text>
      <Link href="/login">Login using BFF</Link>
    </View>
  );
};

export default Page;
