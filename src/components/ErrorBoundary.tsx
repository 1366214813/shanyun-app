import React, { Component, ReactNode } from 'react';
import { View, Text, TouchableOpacity, ScrollView, StyleSheet } from 'react-native';
import { logError } from '../utils/logger';

type Props = { children: ReactNode };
type State = { hasError: boolean; error: Error | null };

export class ErrorBoundary extends Component<Props, State> {
  state: State = { hasError: false, error: null };

  static getDerivedStateFromError(error: Error) {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, info: any) {
    logError('CRASH', error.message, info?.componentStack || error.stack);
  }

  render() {
    if (this.state.hasError) {
      return (
        <View style={styles.container}>
          <Text style={styles.title}>应用出错了</Text>
          <ScrollView style={styles.scroll}>
            <Text style={styles.message}>{this.state.error?.message}</Text>
            <Text style={styles.stack}>{this.state.error?.stack}</Text>
          </ScrollView>
          <TouchableOpacity style={styles.btn} onPress={() => this.setState({ hasError: false, error: null })}>
            <Text style={styles.btnText}>重试</Text>
          </TouchableOpacity>
        </View>
      );
    }
    return this.props.children;
  }
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#FFF0F0', padding: 20, justifyContent: 'center' },
  title: { fontSize: 20, fontWeight: 'bold', color: '#D32F2F', textAlign: 'center', marginBottom: 16 },
  scroll: { maxHeight: 300, backgroundColor: '#fff', borderRadius: 8, padding: 12, marginBottom: 16 },
  message: { fontSize: 14, color: '#333', marginBottom: 8 },
  stack: { fontSize: 11, color: '#999', fontFamily: 'monospace' },
  btn: { backgroundColor: '#6C5CE7', borderRadius: 8, padding: 14, alignItems: 'center' },
  btnText: { color: '#fff', fontSize: 16, fontWeight: '600' },
});
