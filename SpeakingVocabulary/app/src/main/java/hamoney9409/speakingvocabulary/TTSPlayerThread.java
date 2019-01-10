package hamoney9409.speakingvocabulary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import hamoney9409.speakingvocabulary.dataformat.WORD_DATA;

/**
 * Created by 상범 on 2017-05-30.
 */

public class TTSPlayerThread extends Thread
{
    private ArrayList<WORD_DATA> m_wordData = new ArrayList<>(16);
    private ReadWriteLock m_dataLock = new ReentrantReadWriteLock();

    public WriteData StartWrite()
    {
        return new WriteData();
    }

    public class WriteData
    {
        public void EndWrite()
        {
            m_dataLock.writeLock().unlock();
        }

        public WriteData()
        {
            m_dataLock.writeLock().lock();
        }

        public WriteData Clear()
        {
            m_wordData.clear();
            return this;
        }

        public WriteData Add(WORD_DATA data)
        {
            m_wordData.add(data);
            return this;
        }

        public WriteData Add(Collection<WORD_DATA> dataCollection)
        {
            m_wordData.addAll(dataCollection);
            return this;
        }

        public WriteData Add(WORD_DATA[] dataArray)
        {
            for(WORD_DATA data : dataArray)
            {
                m_wordData.add(data);
            }

            return this;
        }

        public WriteData Remove(WORD_DATA data)
        {
            m_wordData.remove(data);
            return this;
        }

    }
    private int m_index = 0;

    public enum STATE {PAUSED, PLAY, QUIT};
    private STATE m_state = STATE.PAUSED;
    private ReadWriteLock m_stateLock = new ReentrantReadWriteLock();

    public void SetState(STATE state)
    {
        m_stateLock.writeLock().lock();
        m_state = state;
        m_stateLock.writeLock().unlock();
    }

    public STATE GetState()
    {
        STATE result;
        m_stateLock.readLock().lock();
        result = m_state;
        m_stateLock.readLock().unlock();
        return result;
    }


    @Override
    public void run()
    {
        while(true)
        {
            if (interrupted())
                break;

            m_stateLock.readLock().lock();
            STATE currentState = m_state;
            m_stateLock.readLock().unlock();

            if (currentState == STATE.QUIT)
                break;

            switch(currentState)
            {
                case PAUSED:
                    try
                    {
                        sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        break;
                    }
                    break;

                case PLAY:
                    try
                    {
                        m_dataLock.readLock().lock();

                        int dataSize = m_wordData.size();
                        if (dataSize <= 0)
                        {
                            m_dataLock.readLock().unlock();
                            sleep(100);
                            break;
                        }

                        if (dataSize <= m_index)
                        {
                            m_index = 0;
                        }
                        WORD_DATA wordData = m_wordData.get(m_index);
                        m_dataLock.readLock().unlock();

                        // 재생
                        TTSManager manager = TTSManager.getInstance();
                        long sleepTime = 100;
                        do
                        {
                            if (manager == null)
                                break;

                            if (manager.IsSpeaking())
                                break;

                            m_index += 1;
                            if (dataSize <= m_index)
                            {
                                m_index = 0;
                            }

                            sleepTime = manager.SpeakWord(wordData.englishWord, wordData.mean);

                        } while(false);

                        // 재생시간 예측수치만큼 대기
                        sleep(sleepTime);

                        // 재생 끝날때까지 대기
                        do
                        {
                            if (manager == null)
                            {
                                sleep(100);
                                continue;
                            }

                            if (manager.IsSpeaking())
                            {
                                sleep(100);
                                continue;
                            }

                            break;

                        } while(true);

                        // 1초 더 대기
                        sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        break;
                    }

                    break;
            } // switch(currentState)


        } // while(true)
    }

}
