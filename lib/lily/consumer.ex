defmodule Lily.Consumer do
  use Nostrum.Consumer

  alias Lily.Commands

  def start_link, do: Consumer.start_link(__MODULE__)

  def handle_event({:MESSAGE_CREATE, msg, _ws_state}) when msg.author.bot == false do
    if String.starts_with?(msg.content, "lily!") do
      content = String.slice(msg.content, 5..-1)
      [head | tail] = String.split(content)

      case Commands.fetch_command(head) do
        nil -> :ignore
        command ->
          spawn(fn -> command.(msg, tail) end)
          :ok
      end
    end
  end

  def handle_event(_event), do: :noop
end