import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Alert, KeyboardAvoidingView, Platform, ScrollView } from 'react-native';
import { supabase } from '../config/supabase';

type Props = { onSkip?: () => void };

export default function AuthScreen({ onSkip }: Props) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    if (!email.trim() || !password.trim()) {
      Alert.alert('提示', '请输入邮箱和密码');
      return;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email.trim())) {
      Alert.alert('提示', '请输入正确的邮箱格式（如：abc@example.com）');
      return;
    }
    if (password.length < 6) {
      Alert.alert('提示', '密码至少 6 位');
      return;
    }

    setLoading(true);
    try {
      const { error } = await supabase.auth.signInWithPassword({ email: email.trim(), password });
      if (error) {
        Alert.alert('错误', error.message);
      }
    } catch (e: any) {
      Alert.alert('错误', e.message || '网络异常');
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView style={styles.container} behavior={Platform.OS === 'ios' ? 'padding' : 'height'}>
      <ScrollView contentContainerStyle={styles.scroll} keyboardShouldPersistTaps="handled">
        <View style={styles.header}>
          <Text style={styles.logo}>🫘</Text>
          <Text style={styles.title}>金豆库管</Text>
          <Text style={styles.subtitle}>服装店库存与吊牌打印工具</Text>
        </View>

        <View style={styles.form}>
          <Text style={styles.formTitle}>登录</Text>

          <Text style={styles.label}>邮箱</Text>
          <TextInput
            style={styles.input}
            value={email}
            onChangeText={setEmail}
            placeholder="请输入邮箱"
            keyboardType="email-address"
            autoCapitalize="none"
            autoCorrect={false}
          />

          <Text style={styles.label}>密码</Text>
          <TextInput
            style={styles.input}
            value={password}
            onChangeText={setPassword}
            placeholder="请输入密码（至少6位）"
            secureTextEntry
          />

          <TouchableOpacity style={[styles.btn, loading && styles.btnDisabled]} onPress={handleLogin} disabled={loading}>
            <Text style={styles.btnText}>{loading ? '处理中...' : '登录'}</Text>
          </TouchableOpacity>
        </View>

        {onSkip && (
          <TouchableOpacity style={styles.skipBtn} onPress={onSkip}>
            <Text style={styles.skipText}>跳过，先用本地模式 →</Text>
          </TouchableOpacity>
        )}
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F5F6FA' },
  scroll: { flexGrow: 1, justifyContent: 'center', padding: 24 },
  header: { alignItems: 'center', marginBottom: 40 },
  logo: { fontSize: 64 },
  title: { fontSize: 28, fontWeight: 'bold', color: '#333', marginTop: 8 },
  subtitle: { fontSize: 14, color: '#999', marginTop: 4 },
  form: { backgroundColor: '#fff', borderRadius: 16, padding: 24 },
  formTitle: { fontSize: 20, fontWeight: '600', color: '#333', marginBottom: 20 },
  label: { fontSize: 13, color: '#666', marginBottom: 6 },
  input: { borderWidth: 1, borderColor: '#E0E0E0', borderRadius: 10, padding: 12, fontSize: 15, marginBottom: 16 },
  btn: { backgroundColor: '#6C5CE7', borderRadius: 10, padding: 14, alignItems: 'center', marginTop: 8 },
  btnDisabled: { opacity: 0.6 },
  btnText: { color: '#fff', fontSize: 16, fontWeight: '600' },
  skipBtn: { alignItems: 'center', marginTop: 24 },
  skipText: { color: '#999', fontSize: 14 },
});
